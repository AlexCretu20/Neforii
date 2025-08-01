package models.post;

import java.util.UUID;

public record PostRequestDto(
        String title,
        String content,
        String author,
        String subreddit,
        String imagePath

) {}
