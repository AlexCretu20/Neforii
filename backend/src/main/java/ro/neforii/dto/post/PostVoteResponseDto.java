package ro.neforii.dto.post;

import lombok.Builder;

@Builder
public record PostVoteResponseDto(
        Integer upvotes,
        Integer downvotes,
        Integer score,
        String userVote

) {
}
