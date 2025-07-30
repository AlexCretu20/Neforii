package ro.neforii.service;

import org.springframework.stereotype.Service;
import ro.neforii.exception.VoteNotFoundException;
import ro.neforii.model.Comment;
import ro.neforii.model.Post;
import ro.neforii.model.User;
import ro.neforii.model.Vote;
import ro.neforii.repository.CommentRepository;
import ro.neforii.repository.PostRepository;
import ro.neforii.repository.UserRepository;
import ro.neforii.repository.VoteRepository;
import ro.neforii.utils.logger.Logger;
import ro.neforii.utils.logger.LoggerType;

import java.util.Optional;
import java.util.UUID;

@Service
public class VoteService implements IVoteService {
    private final UserRepository userRepository;
    private final PostRepository postRepository;
    private final VoteRepository voteRepository;
    private final CommentRepository commentRepository;

    public VoteService(PostRepository postRepository, VoteRepository voteRepository, CommentRepository commentRepository, UserRepository userRepository) {
        this.postRepository = postRepository;
        this.voteRepository = voteRepository;
        this.commentRepository = commentRepository;
        this.userRepository = userRepository;
    }

    public String createVote(UUID userId, UUID postId, UUID commentId, boolean isUpvote) {
        User user = userRepository.findById(userId).orElseThrow();

        if (postId != null && commentId == null) {
            Post post = postRepository.findById(postId).orElseThrow();
            Optional<Vote> existingVoteOpt = voteRepository.findByPostAndUser(post, user);

            if (existingVoteOpt.isPresent()) {
                Vote vote = existingVoteOpt.get();
                vote.setUpvote(isUpvote);
                voteRepository.save(vote);
            } else {
                Vote vote = new Vote(isUpvote, post, null, user);
                voteRepository.save(vote);
            }
        } else if (commentId != null && postId == null) {
            Comment comment = commentRepository.findById(commentId).orElseThrow();
            Optional<Vote> existingVoteOpt = voteRepository.findByCommentAndUser(comment, user);

            if (existingVoteOpt.isPresent()) {
                Vote vote = existingVoteOpt.get();
                vote.setUpvote(isUpvote); // update vot existent
                voteRepository.save(vote);
            } else {
                Vote vote = new Vote(isUpvote, null, comment, user);
                voteRepository.save(vote);
            }
        } else {
            throw new IllegalArgumentException("Vote must target either a post or a comment");
        }

        return "You have successfully voted!";
    }


    public void deleteVote(UUID voteId) {
        voteRepository.deleteById(voteId);
        Logger.log(LoggerType.INFO, "Vote with ID " + voteId + " deleted successfully.");
    }

    public Vote getVoteById(UUID voteId) {
        return voteRepository.findById(voteId).orElseThrow(() -> {
            Logger.log(LoggerType.DEBUG, "Vote with ID " + voteId + " not found.");
            return new VoteNotFoundException(voteId);
        });
    }

//    public int findVoteId(int userId, Integer postId, Integer commentId) {
//        return voteRepository.findAll().stream()
//                .filter(vote ->
//                        vote.getUserId() == userId && ((postId != null && postId.equals(vote.getPostId())) || (commentId != null && commentId.equals(vote.getCommentId()))))
//                .mapToInt(Vote::getId)
//                .findFirst()
//                .orElse(0);
//    }

    public String getVoteTypeForUser(Comment comment, User user){
        Optional<Vote> vote = voteRepository.findByCommentAndUser(comment, user);

        if(vote.isEmpty()){
            return "none";
        }

        return vote.get().isUpvote() ? "up" : "down";
    }

    public void deleteVoteForComment(Comment comment, User user) {
        Optional<Vote> vote = voteRepository.findByCommentAndUser(comment, user);
        vote.ifPresent(v -> voteRepository.deleteById(v.getId()));
    }

}
