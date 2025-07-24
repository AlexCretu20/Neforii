package ro.neforii.dto.post;

import java.time.LocalDateTime;

public record PostResponseDto(
        Integer id,
        String title,
        String content,
        Integer userId,
        LocalDateTime createdAt,
        Boolean isAwarded,
        String imagePath
) {
}

