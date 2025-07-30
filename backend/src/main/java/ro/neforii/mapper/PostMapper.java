package ro.neforii.mapper;

import org.springframework.stereotype.Component;
import ro.neforii.dto.post.PostRequestDto;
import ro.neforii.dto.post.PostResponseDto;
import ro.neforii.model.Post;
import ro.neforii.model.User;
import ro.neforii.model.Vote;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Component
public class PostMapper {

    public PostResponseDto postToPostResponseDto(Post post, UUID currentUserId) {
        if (post == null) return null;

        List<Vote> votes = post.getVotes() != null ? post.getVotes() : List.of();

        int upvotes = (int) votes.stream().filter(Vote::isUpvote).count();
        int downvotes = votes.size() - upvotes;
        int score = upvotes - downvotes;

        String userVote = votes.stream()
                .filter(v -> v.getUser().getId().equals(currentUserId))
                .findFirst()
                .map(v -> v.isUpvote() ? "up" : "down")
                .orElse(null);

        int commentCount = post.getComments() != null ? post.getComments().size() : 0;

        return PostResponseDto.builder()
                .id(post.getId())
                .title(post.getTitle())
                .content(post.getContent())
                .author(post.getAuthor())
                .subreddit(post.getSubreddit()) // presupun ca subreddit e un obiect cu nume
                .upvotes(upvotes)
                .downvotes(downvotes)
                .score(score)
                .commentCount(commentCount)
                .userVote(userVote)
                .createdAt(post.getCreatedAt())
                .updatedAt(post.getUpdatedAt())
                .build();
    }

    public Post postRequestDtoToPost(PostRequestDto postRequestDto, User user) {
        if (postRequestDto == null) return null;

        return Post.builder()
                .title(postRequestDto.title())
                .content(postRequestDto.content())
                .author(postRequestDto.author())
                .user(user)
                .subreddit(postRequestDto.subreddit())
                .createdAt(LocalDateTime.now())
                .build();
    }
}


