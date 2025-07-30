package ro.neforii.dto.vote;

import java.util.UUID;

public record VoteRequestDto (
        boolean isUpvote,
        UUID userId,
        UUID postId,
        UUID commentId
)
{}
