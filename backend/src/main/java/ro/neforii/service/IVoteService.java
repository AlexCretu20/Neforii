package ro.neforii.service;

import ro.neforii.model.Vote;

public interface IVoteService {
    String createVote(int userId, Integer postId, Integer commentId, boolean isUpvote);

    void deleteVote(int voteId);

    Vote getVoteById(int voteId);
}
