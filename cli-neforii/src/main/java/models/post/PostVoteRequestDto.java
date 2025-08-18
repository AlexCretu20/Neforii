package models.post;

public record PostVoteRequestDto(
        String voteType // up, down, none
) {}
