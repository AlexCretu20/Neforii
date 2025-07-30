package ro.neforii.mapper;

import org.springframework.stereotype.Component;
import ro.neforii.dto.post.PostRequestDto;
import ro.neforii.dto.post.PostResponseDto;
import ro.neforii.model.Post;
import ro.neforii.model.Vote;

import java.util.UUID;

@Component
public class PostMapper {

    public PostResponseDto postToPostResponseDto(Post post, UUID currentUserId) {
        if (post == null) return null;

        int upvotes = (int) post.getVotes().stream().filter(Vote::isUpvote).count();
        int downvotes = (int) post.getVotes().stream().filter(v -> !v.isUpvote()).count();
        int score = upvotes - downvotes;

        String userVote = post.getVotes().stream()
                .filter(v -> v.getUser().getId().equals(currentUserId))
                .findFirst()
                .map(v -> v.isUpvote() ? "up" : "down")
                .orElse(null);

        return PostResponseDto.builder()
                .id(post.getId())
                .title(post.getTitle())
                .content(post.getContent())
                .author(post.getAuthor())
                .subreddit(post.getSubreddit()) // presupun ca subreddit e un obiect cu nume
                .upvotes(upvotes)
                .downvotes(downvotes)
                .score(score)
                .commentCount(post.getComments().size())
                .userVote(userVote)
                .createdAt(post.getCreatedAt())
                .updatedAt(post.getUpdatedAt())
                .build();
    }

    public Post postRequestDtoToPost(PostRequestDto dto) {
        if (dto == null) return null;

        Post post = new Post();
        post.setTitle(dto.title());
        post.setContent(dto.content());
        post.setAuthor(dto.author());
        post.setSubreddit(dto.subreddit());
        return post;
    }
}


