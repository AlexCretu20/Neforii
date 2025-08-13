package ro.neforii.service;

import org.springframework.stereotype.Service;
import ro.neforii.exception.CommentNotFoundException;
import ro.neforii.exception.PostNotFoundException;
import ro.neforii.exception.VoteNotFoundException;
import ro.neforii.exception.user.UserNotFoundException;
import ro.neforii.model.*;
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
    private static final String LOG_PREFIX = "VoteService: ";
    public VoteService(PostRepository postRepository, VoteRepository voteRepository, CommentRepository commentRepository, UserRepository userRepository) {
        this.postRepository = postRepository;
        this.voteRepository = voteRepository;
        this.commentRepository = commentRepository;
        this.userRepository = userRepository;
    }
// TODO de facut mai clean
    public String createVote(UUID userId, UUID postId, UUID commentId, boolean isUpvote) {
        User user = userRepository.findById(userId).orElseThrow();

        if ((postId == null && commentId == null) || (postId != null && commentId != null)) {
            throw new IllegalArgumentException("Vote must target either a post or a comment, not both or neither.");
        }

        Vote vote;

        if (postId != null) {
            Post post = postRepository.findById(postId).orElseThrow();
            vote = voteRepository.findByPostAndUser(post, user)
                    .orElse(new Vote(false, post, null, user)); // default to false, update next line
            vote.setUpvote(isUpvote);
        } else {
            Comment comment = commentRepository.findById(commentId).orElseThrow();
            vote = voteRepository.findByCommentAndUser(comment, user)
                    .orElse(new Vote(false, null, comment, user));
            vote.setUpvote(isUpvote);
        }

        voteRepository.save(vote);
        return "You have successfully voted!";
    }

    public Vote createOrUpdateVoteForComment(UUID userId, UUID commentId, VoteType voteType) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException("User not found with ID: " + userId));
        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new CommentNotFoundException("Comment not found with ID: " + commentId));

        Optional<Vote> existingVoteOpt = voteRepository.findByCommentAndUser(comment, user);

        if (voteType == VoteType.NONE) {
            existingVoteOpt.ifPresent(voteRepository::delete);
            return null;
        }

        Vote vote = existingVoteOpt.orElseGet(() ->
                new Vote(voteType == VoteType.UP, null, comment, user)
        );
        vote.setUpvote(voteType == VoteType.UP);

        return voteRepository.save(vote);
    }

    public Vote createOrUpdateVoteForPost(UUID userId, UUID postId, VoteType voteType) {
        Logger.log(LoggerType.DEBUG, LOG_PREFIX + "Processing vote type " + voteType + " for post " + postId + " by user " + userId);
        try {
            User user = userRepository.findById(userId)
                    .orElseThrow(() -> new UserNotFoundException("User not found with ID: " + userId));
            Post post = postRepository.findById(postId)
                    .orElseThrow(() -> new PostNotFoundException("Post not found with ID: " + postId));

            Optional<Vote> existingVoteOpt = voteRepository.findByPostAndUser(post, user);

            if (voteType == VoteType.NONE) {
                if (existingVoteOpt.isPresent()) {
                    Logger.log(LoggerType.INFO, LOG_PREFIX + "Removing vote from post " + postId + " by user " + userId);
                    voteRepository.delete(existingVoteOpt.get());
                }
                return null;
            }

            Vote vote = existingVoteOpt.orElseGet(() ->
                    new Vote(voteType == VoteType.UP, post, null, user)
            );
            vote.setUpvote(voteType == VoteType.UP);

            Vote savedVote = voteRepository.save(vote);
            Logger.log(LoggerType.INFO, LOG_PREFIX + "Saved " + (voteType == VoteType.UP ? "upvote" : "downvote") + " for post " + postId);

            Post updatedPost = postRepository.findById(postId)
                    .orElseThrow(() -> new PostNotFoundException("Post not found after voting: " + postId));

            evaluatePostAward(updatedPost);
            return savedVote;
        } catch (Exception e) {
            Logger.log(LoggerType.ERROR, LOG_PREFIX + "Error processing vote: " + e.getMessage());
            throw e;
        }
    }

    public void evaluatePostAward(Post post) {
        long upvoteCount = post.getVotes().stream()
                .filter(Vote::isUpvote)
                .count();

        boolean shouldBeAwarded = upvoteCount >= 2;

        if (shouldBeAwarded != post.isAwarded()) {
            post.setAwarded(shouldBeAwarded);
            postRepository.save(post);
        }
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

    public String getVoteTypeForUser(Comment comment, User user) {
        Logger.log(LoggerType.DEBUG, LOG_PREFIX + "Getting vote type for comment " + comment.getId() + " by user " + user.getUsername());
        try {
            Optional<Vote> vote = voteRepository.findByCommentAndUser(comment, user);
            String voteType = vote.isEmpty() ? "none" : (vote.get().isUpvote() ? "up" : "down");
            Logger.log(LoggerType.DEBUG, LOG_PREFIX + "Vote type for comment " + comment.getId() + " by user " + user.getUsername() + ": " + voteType);
            return voteType;
        } catch (Exception e) {
            Logger.log(LoggerType.ERROR, LOG_PREFIX + "Error retrieving vote type for comment " + comment.getId() + ": " + e.getMessage());
            throw e;
        }
    }

    public void deleteVoteForComment(Comment comment, User user) {
        Logger.log(LoggerType.DEBUG, LOG_PREFIX + "Attempting to delete vote for comment " + comment.getId() + " by user " + user.getUsername());
        try {
            Optional<Vote> vote = voteRepository.findByCommentAndUser(comment, user);
            if (vote.isPresent()) {
                voteRepository.deleteById(vote.get().getId());
                Logger.log(LoggerType.INFO, LOG_PREFIX + "Vote deleted for comment " + comment.getId() + " by user " + user.getUsername());
            } else {
                Logger.log(LoggerType.DEBUG, LOG_PREFIX + "No vote found to delete for comment " + comment.getId() + " by user " + user.getUsername());
            }
        } catch (Exception e) {
            Logger.log(LoggerType.ERROR, LOG_PREFIX + "Error deleting vote for comment " + comment.getId() + ": " + e.getMessage());
            throw e;
        }
    }

    public void deleteByUserIdAndPostId(UUID userId, UUID postId) {
        Logger.log(LoggerType.DEBUG, LOG_PREFIX + "Attempting to delete vote for post " + postId + " by user " + userId);
        try {
            User user = userRepository.findById(userId)
                    .orElseThrow(() -> {
                        Logger.log(LoggerType.WARNING, LOG_PREFIX + "User not found with ID: " + userId);
                        return new UserNotFoundException("User not found");
                    });

            Post post = postRepository.findById(postId)
                    .orElseThrow(() -> {
                        Logger.log(LoggerType.WARNING, LOG_PREFIX + "Post not found with ID: " + postId);
                        return new PostNotFoundException("Post not found");
                    });

            voteRepository.deleteByUserAndPost(user, post);
            Logger.log(LoggerType.INFO, LOG_PREFIX + "Vote deleted for post " + postId + " by user " + user.getUsername());
        } catch (Exception e) {
            Logger.log(LoggerType.ERROR, LOG_PREFIX + "Error deleting vote for post " + postId + ": " + e.getMessage());
            throw e;
        }
    }
}
