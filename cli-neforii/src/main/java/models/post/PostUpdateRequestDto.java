package models.post;

public record PostUpdateRequestDto(
        String title,
        String content
) {}
