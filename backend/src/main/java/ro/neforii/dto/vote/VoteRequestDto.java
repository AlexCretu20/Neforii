package ro.neforii.dto.vote;

public record VoteRequestDto (
        boolean isUpvote,
        Integer userId,
        Integer postId,
        Integer commentId
)
{}
