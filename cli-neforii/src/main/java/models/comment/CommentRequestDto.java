package models.comment;

import java.util.UUID;

public record CommentRequestDto(
        String content,
        String author,
        UUID parentId

) {}