package ro.neforii.dto.comment.vote;

import jakarta.validation.constraints.NotBlank;

public record VoteRequestDto(
        @NotBlank(message = "Vote type must pe provided")
        String voteType //up, down sau none
) {
}
