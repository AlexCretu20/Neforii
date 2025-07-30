package models.post;

import java.util.UUID;

public record PostRequestDto(
        String title,
        String content,
        String imagePath,
        String author,
        UUID userId
) {
}
