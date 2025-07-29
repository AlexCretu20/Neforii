package models.post;

import java.time.LocalDateTime;

public record PostResponseDto(
        Integer id,
        String title,
        String content,
        String author,
        Integer userId,
        LocalDateTime createdAt,
        Boolean isAwarded,
        String imagePath
) {
}
