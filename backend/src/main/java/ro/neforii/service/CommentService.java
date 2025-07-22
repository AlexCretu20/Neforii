package ro.neforii.service;

import ro.neforii.exception.CommentNotFoundException;
import ro.neforii.exception.PostNotFoundException;
import ro.neforii.model.Comment;
import ro.neforii.model.User;
import ro.neforii.repository.CommentRepository;
import ro.neforii.repository.PostRepository;
import ro.neforii.repository.UserRepository;
import ro.neforii.repository.VoteRepository;
import ro.neforii.utils.logger.Logger;
import ro.neforii.utils.logger.LoggerType;

import java.util.List;
import java.util.Optional;

public class CommentService implements IVotable {
    private final CommentRepository commentRepo;
    private final UserRepository userRepo;
    private final PostRepository postRepo;
    private final VoteRepository voteRepo;

    public CommentService(CommentRepository commentRepo, UserRepository userRepo, PostRepository postRepo, VoteRepository voteRepo) {
        this.commentRepo = commentRepo;
        this.userRepo = userRepo;
        this.postRepo = postRepo;
        this.voteRepo = voteRepo;
    }

    public List<Comment> getComments() {
        return commentRepo.findAll();
    }

    public Comment getComment(int id) {
        return commentRepo.findById(id).orElseThrow(() -> {
            Logger.log(LoggerType.FATAL, "Comment with id=" + id + " not found in getComment()");
            return new CommentNotFoundException("Comment with id " + id + " does not exist");
        });
    }

    public void createCommentOnPost(String text, User user, int postId) {
        if (postRepo.findById(postId).isEmpty()) {
            Logger.log(LoggerType.FATAL, "Attempt to comment on nonexistent post id=" + postId);
            throw new PostNotFoundException("Post with id=" + postId + " not found");
        }
        Comment comment = new Comment(text, user, postId, null);
        commentRepo.save(comment);
        Logger.log(LoggerType.INFO, "Created comment id=" + comment.getId() + " on post id=" + postId);
    }

    public void createReplyToComment(String text, User user, int parentCommentId) {
        if (commentRepo.findById(parentCommentId).isEmpty()) {
            Logger.log(LoggerType.FATAL, "Attempt to reply to nonexistent comment id=" + parentCommentId);
            throw new CommentNotFoundException("Could not find comment with id=" + parentCommentId);
        }
        Comment reply = new Comment(text, user, null, parentCommentId);
        commentRepo.save(reply);
        Logger.log(LoggerType.INFO, "Created reply id=" + reply.getId() + " to comment id=" + parentCommentId);
    }

    public void updateComment(int id, Comment updatedComment) {
        Comment existing = getComment(id);
        try {
            commentRepo.update(updatedComment);
            Logger.log(LoggerType.INFO, "Comment updated successfully!");
        } catch (Exception e) {
            Logger.log(LoggerType.FATAL, "Failed to update comment id=" + id);
        }
    }

    public void deleteComment(int id) {
        getComment(id);
        try {
            commentRepo.deleteById(id);
            Logger.log(LoggerType.INFO, "Comment id=" + id + " deleted successfully");
        } catch (Exception e) {
            Logger.log(LoggerType.FATAL, "Failed to delete comment id=" + id);
        }
    }


    public List<Comment> getRepliesForComment(int commentId) {
        getComment(commentId);
        return commentRepo.findByCommentId(commentId);
    }

    public Integer findPostIdForComment(int commentId) {
        Optional<Comment> currentOptional = commentRepo.findById(commentId);

        while (currentOptional.isPresent()) {
            Comment current = currentOptional.get();
            if (current.getPostId() != null) {
                return current.getPostId();
            }
            Integer parentId = current.getParentCommentId();
            if (parentId == null) break;
            currentOptional = commentRepo.findById(parentId);
        }

        throw new IllegalStateException("Could not trace postId for comment id=" + commentId);
    }



    public List<Comment> getTopLevelComments(int postId) {
        return commentRepo.findByPostId(postId).stream()
                .filter(c -> c.getParentCommentId() == null)
                .toList();
    }

    public int displayUpvotes(int id) {
        return voteRepo.countVotesByCommentId(id, true);
    }

    public int displayDownvotes(int id) {
        return voteRepo.countVotesByCommentId(id, false);
    }

}
