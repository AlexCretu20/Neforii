package ro.neforii.service;

import jakarta.transaction.Transactional;
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
//hat fixes the above, keeps your behavior (NONE removes), and is concurrency-aware:
//
//    @Transactional
//    public VoteResult upsertPostVote(UUID userId, UUID postId, VoteType voteType) {
//        if (voteType == null) throw new IllegalArgumentException("voteType is required");
//
//        // Load once, fail fast
//        User user = userRepository.findById(userId)
//                .orElseThrow(() -> new UserNotFoundException("User not found: " + userId));
//        Post post = postRepository.findById(postId)
//                .orElseThrow(() -> new PostNotFoundException("Post not found: " + postId));
//
//        // Lock the (potential) row to avoid duplicate votes under concurrency
//        // Define this method with @Lock(PESSIMISTIC_WRITE) in the repository
//        Optional<Vote> existingOpt = voteRepository.findByPostAndUserForUpdate(post, user);
//
//        if (voteType == VoteType.NONE) {
//            if (existingOpt.isPresent()) {
//                voteRepository.delete(existingOpt.get());
//                // If you track counters on Post, decrement here
//                // adjustPostCounters(post, existingOpt.get(), null);
//                // postRepository.save(post);
//                registerAfterCommit(() -> evaluatePostAward(post));
//                return VoteResult.removed();
//            }
//            return VoteResult.noop(); // nothing to remove
//        }
//
//        boolean wantUp = (voteType == VoteType.UP);
//
//        if (existingOpt.isPresent()) {
//            Vote existing = existingOpt.get();
//            if (existing.isUpvote() == wantUp) {
//                return VoteResult.noop(existing); // idempotent no-op
//            }
//            existing.setUpvote(wantUp);
//            Vote saved = voteRepository.save(existing);
//            // adjustPostCounters(post, existing /*old value known*/, saved);
//            // postRepository.save(post);
//            registerAfterCommit(() -> evaluatePostAward(post));
//            return VoteResult.updated(saved);
//        } else {
//            Vote created = voteRepository.save(new Vote(wantUp, post, null, user));
//            // adjustPostCounters(post, null, created);
//            // postRepository.save(post);
//            registerAfterCommit(() -> evaluatePostAward(post));
//            return VoteResult.created(created);
//        }
//    }

    //    private static void registerAfterCommit(Runnable task) {
//        org.springframework.transaction.support.TransactionSynchronizationManager.registerSynchronization(
//                new org.springframework.transaction.support.TransactionSynchronizationAdapter() {
//                    @Override public void afterCommit() { task.run(); }
//                }
//        );
//    }
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

    @Transactional
    public Vote createOrUpdateVoteForPost(UUID userId, UUID postId, VoteType voteType) {
        if (voteType == null) throw new IllegalArgumentException("voteType is required");

        // Validate existence (and load only IDs you need)
        userRepository.existsById(userId); // or throw if needed
        postRepository.existsById(postId);

        // Nuke any existing rows to avoid “phantom opposite vote”
        voteRepository.deleteByPostIdAndUserId(postId, userId);

        if (voteType == VoteType.NONE) {
            afterCommit(() -> evaluatePostAwardById(postId)); // use byId to avoid entity state issues
            return null;
        }
        Post post = postRepository.findById(postId).orElseThrow(() -> {
            Logger.log(LoggerType.DEBUG, LOG_PREFIX + "Post with ID " + postId + " not found.");
            return new PostNotFoundException("Post not found with ID: " + postId);
        });
        User user = userRepository.findById(userId).orElseThrow(() -> {
            Logger.log(LoggerType.DEBUG, LOG_PREFIX + "User with ID " + userId + " not found.");
            return new UserNotFoundException("User not found with ID: " + userId);
        });
        Vote v = new Vote(voteType == VoteType.UP, /*post*/ post, /*comment*/ null, /*user*/ user);
        Vote saved = voteRepository.save(v);

        afterCommit(() -> evaluatePostAwardById(postId));
        return saved;
    }

    private static void afterCommit(Runnable r) {
        org.springframework.transaction.support.TransactionSynchronizationManager.registerSynchronization(
                new org.springframework.transaction.support.TransactionSynchronizationAdapter() {
                    @Override public void afterCommit() { r.run(); }
                }
        );
    }


    public void evaluatePostAwardById(UUID postId) {
        Post post = postRepository.findById(postId).orElseThrow(() -> {
            Logger.log(LoggerType.DEBUG, LOG_PREFIX + "Post with ID " + postId + " not found.");
            return new PostNotFoundException("Post not found with ID: " + postId);
        });
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
