package ro.neforii.dto.post;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;


public record PostRequestDto(

        @NotBlank(message = "The title is required.")
        @Size(min = 3, max = 300)
        String title,

        @Size(max = 10000)
        String content,

        @NotBlank(message = "The author is required.")
        String author,

        String subreddit

) {
}


