package ro.neforii.dto.vote;

import ro.neforii.model.VoteType;

public record VoteRequestDto(
        String voteType // mai bine las string decat votetype si mapez in service
) {
}
