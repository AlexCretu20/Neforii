package ro.neforii.dto.vote;

import java.time.LocalDateTime;

public record VoteResponseDto(
        Integer id,
        boolean isUpvote,
        LocalDateTime createdAt,
        Integer postId,
        Integer commentId
) {
}
