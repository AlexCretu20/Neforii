package ro.neforii.dto.post;

import java.time.LocalDateTime;

public record PostResponseDto(
        Integer id,
        String text,
        Integer userId,
        LocalDateTime createdAt,
        Boolean isAwarded
) {
}

