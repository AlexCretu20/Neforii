package ro.neforii.dto.comment.vote;

public record CommentVoteResponseDto(
        int upVotes,
        int downVotes,
        int score,
        String userVote
) {}
