package ro.neforii.dto.comment.update;

import jakarta.validation.constraints.NotBlank;

public record CommentUpdateRequestDto(
        @NotBlank(message = "Comment text can not be blank")
        String content

) {}

