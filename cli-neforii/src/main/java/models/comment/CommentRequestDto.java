package models.comment;

public record CommentRequestDto(
        String content,
        String author,
        Integer parentId
) {}