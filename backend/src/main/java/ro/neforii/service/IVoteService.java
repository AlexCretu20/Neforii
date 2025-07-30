package ro.neforii.service;

import ro.neforii.model.Vote;

import java.util.UUID;

public interface IVoteService {
    String createVote(UUID userId, UUID postId, UUID commentId, boolean isUpvote);

    void deleteVote(UUID voteId);

    Vote getVoteById(UUID voteId);
}