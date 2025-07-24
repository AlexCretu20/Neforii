package ro.neforii.dto.comment.update;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;

public record CommentUpdateRequestDto(
        @NotBlank(message = "Comment text can not be blank")
        String text,

        @Positive(message = "User id must be positive")
        int userId
) {}

