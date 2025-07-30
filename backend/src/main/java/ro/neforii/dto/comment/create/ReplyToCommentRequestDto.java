package ro.neforii.dto.comment.create;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;

import java.util.UUID;

public record ReplyToCommentRequestDto(
        @NotBlank(message = "Comment text can not be blank")
        String text,

        @Positive(message = "User id must be positive")
        UUID userId
) {}
