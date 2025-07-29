package ro.neforii.mapper;

import org.springframework.stereotype.Component;
import ro.neforii.dto.post.PostRequestDto;
import ro.neforii.dto.post.PostResponseDto;
import ro.neforii.model.Post;
import ro.neforii.model.Vote;

@Component
public class PostMapper {

    public PostResponseDto postToPostResponseDto(Post post, Integer currentUserId) {
        if (post == null) return null;

        int upvotes = (int) post.getVotes().stream().filter(Vote::isUpvote).count();
        int downvotes = (int) post.getVotes().stream().filter(v -> !v.isUpvote()).count();
        int score = upvotes - downvotes;

        String userVote = post.getVotes().stream()
                .filter(v -> v.getUser().getId()==currentUserId)
                .findFirst()
                .map(v -> v.isUpvote() ? "up" : "down")
                .orElse(null);

        return new PostResponseDto(
                String.valueOf(post.getId()),
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


