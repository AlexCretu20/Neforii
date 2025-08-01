package models.post;

import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.util.UUID;

public record PostResponseDto(
        UUID id,
        String title,
        String content,
        String author,
        String subreddit,
        Integer upvotes,
        Integer downvotes,
        Integer score,
        Integer commentCount,
        String userVote,
        String createdAt,
        String updatedAt,
        String imagePath

) {
}
