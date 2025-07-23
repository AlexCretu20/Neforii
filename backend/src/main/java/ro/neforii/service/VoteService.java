package ro.neforii.service;

import org.springframework.stereotype.Service;
import ro.neforii.model.Vote;
import ro.neforii.repository.CommentRepository;
import ro.neforii.repository.PostRepository;
import ro.neforii.repository.VoteRepository;
import ro.neforii.utils.logger.Logger;
import ro.neforii.utils.logger.LoggerType;

import java.time.LocalDateTime;
import java.util.Optional;

@Service
public class VoteService implements IVoteService {
    private PostRepository postRepository;
    private VoteRepository voteRepository;
    private CommentRepository commentRepository;

    public VoteService(PostRepository postRepository, VoteRepository voteRepository, CommentRepository commentRepository) {
        this.postRepository = postRepository;
        this.voteRepository = voteRepository;
        this.commentRepository = commentRepository;
    }

    public String createVote(int userId, Integer postId, Integer commentId, boolean isUpvote) {
        int checkIfAlreadyVoted = voteRepository.findVoteByTargetId(userId, postId, commentId).getId();
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

    public Vote getVoteById(int voteId) {
        Optional<Vote> vote = voteRepository.findById(voteId);
        if (vote.isPresent()) {
            return vote.get();
        } else {
            Logger.log(LoggerType.ERROR, "Vote with ID " + voteId + " not found.");
            return null;
        }
    }

//    public int findVoteId(int userId, Integer postId, Integer commentId) {
//        return voteRepository.findAll().stream()
//                .filter(vote ->
//                        vote.getUserId() == userId && ((postId != null && postId.equals(vote.getPostId())) || (commentId != null && commentId.equals(vote.getCommentId()))))
//                .mapToInt(Vote::getId)
//                .findFirst()
//                .orElse(0);
//    }
}
