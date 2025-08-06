package ro.neforii.dto.post;

import java.util.UUID;

public record PostCommentRequestDto(
    String content,
    String author,
    UUID parentId
) {
}
