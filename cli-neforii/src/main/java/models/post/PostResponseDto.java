package models.post;

import java.time.LocalDateTime;

public record PostResponseDto(
        Integer id,
        String title,
        String content,
        String author,
        String subreddit,
        Integer upvotes,
        Integer downvotes,
        Integer score,
        Integer commentCount,
        String userVote,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {
}
