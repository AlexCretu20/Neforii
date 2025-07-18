package service;

import model.Vote;
import repository.CommentRepository;
import repository.PostRepository;
import repository.VoteRepository;
import utils.logger.Logger;
import utils.logger.LoggerType;

import java.time.LocalDateTime;

public class VoteService {
    private PostRepository postRepository;
    private VoteRepository voteRepository;
    private CommentRepository commentRepository;

    public VoteService(PostRepository postRepository, VoteRepository voteRepository, CommentRepository commentRepository) {
        this.postRepository = postRepository;
        this.voteRepository = voteRepository;
        this.commentRepository = commentRepository;
    }

    public String createVote(int userId, Integer postId, Integer commentId, boolean isUpvote) {
        int checkIfAlreadyVoted = findVoteId(userId, postId, commentId);
        if (checkIfAlreadyVoted != 0) {
            voteRepository.deleteById(checkIfAlreadyVoted);
        }
        Vote vote = new Vote(
                isUpvote,
                LocalDateTime.now(),
                postId,
                commentId,
                userId
        );
        voteRepository.save(vote);
        return "You have successfully voted!";
    }

    public void deleteVote(int voteId) {
        voteRepository.deleteById(voteId);
        Logger.log(LoggerType.INFO, "Vote with ID " + voteId + " deleted successfully.");
    }

    public int findVoteId(int userId, Integer postId, Integer commentId) {
        return voteRepository.findAll().stream()
                .filter(vote ->
                        vote.getUserId() == userId && ((postId != null && postId.equals(vote.getPostId())) || (commentId != null && commentId.equals(vote.getCommentId()))))
                .mapToInt(Vote::getId)
                .findFirst()
                .orElse(0);
    }


}
