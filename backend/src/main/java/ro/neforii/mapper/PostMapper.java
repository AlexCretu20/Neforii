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

        return new PostResponseDto(
                post.getId(),
                post.getTitle(),
                post.getContent(),
                post.getAuthor(),
                post.getSubreddit(), // e un String acum
                upvotes,
                downvotes,
                score,
                post.getComments().size(),
                userVote,
                post.getCreatedAt(),
                post.getUpdatedAt()
        );
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


