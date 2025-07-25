package ro.neforii.dto.comment.create;

import jakarta.validation.constraints.NotBlank;

public record CommentOnPostRequestDto(
        @NotBlank(message = "Comment text can not be blank")
        String content,

        @NotBlank(message = "Author (username) is required")
        String author,

        Integer parentId
) {}
