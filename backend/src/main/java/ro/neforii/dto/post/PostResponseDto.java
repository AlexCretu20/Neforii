package ro.neforii.dto.post;

import java.time.LocalDateTime;

public record PostResponseDto(
        String id,
        String title,
        String content,
        String author,
        String subreddit,
        int upvotes,
        int downvotes,
        int score,
        int commentCount,
        String userVote, // poate fi null
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {}