package ro.neforii.dto.post;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Builder;

import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.util.UUID;

@Builder
public record PostResponseDto(
        UUID id,
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
        LocalDateTime updatedAt,
        String imagePath
) {}