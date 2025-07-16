package service;

import model.Vote;
import repository.CommentRepository;
import repository.PostRepository;
import repository.VoteRepository;
import utils.logger.Logger;
import utils.logger.LoggerType;

import java.time.LocalDateTime;

public class VoteService {
    private static VoteService instance;
    private PostRepository postRepository;
    private VoteRepository voteRepository;
    private CommentRepository commentRepository;


    private VoteService() {
    }

    public static VoteService getInstance() {
        if (instance == null) {
            instance = new VoteService();
        }
        return instance;
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
//        commentService.getCommentById(entityId).getVotes().add(vote);

//        if (entityType == EntityType.POST) {
//            PostService.getInstance(postRepository, voteRepository, commentRepository).AddVote(entityId,vote);
////            postService.getPostById(entityId).setAwarded(true)
//        }
//        else {
//            CommentService.getInstance().addVote(entityId, vote);
//        }
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
