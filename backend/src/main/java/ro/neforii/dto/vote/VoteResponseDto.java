package ro.neforii.dto.vote;

import java.time.LocalDateTime;
import java.util.UUID;

public record VoteResponseDto(
        UUID id,
        boolean isUpvote,
        LocalDateTime createdAt,
        UUID postId,
        UUID commentId
) {
}
