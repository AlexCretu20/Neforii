package ro.neforii.service;

import org.springframework.stereotype.Service;
import ro.neforii.exception.CommentNotFoundException;
import ro.neforii.exception.PostNotFoundException;
import ro.neforii.model.Comment;
import ro.neforii.model.Post;
import ro.neforii.model.User;
import ro.neforii.repository.CommentRepository;
import ro.neforii.repository.PostRepository;
import ro.neforii.repository.UserRepository;
import ro.neforii.repository.VoteRepository;
import ro.neforii.utils.logger.Logger;
import ro.neforii.utils.logger.LoggerType;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
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

    public Comment getComment(UUID id) {
        return commentRepo.findById(id).orElseThrow(() -> {
            Logger.log(LoggerType.FATAL, "Comment with id=" + id + " not found in getComment()");
            return new CommentNotFoundException("Comment with id " + id + " does not exist");
        });
    }

    public Comment createCommentOnPost(String content, User user, UUID postId) {
        Post post = postRepo.findById(postId).orElseThrow(() -> {
            Logger.log(LoggerType.FATAL, "Attempt to comment on nonexistent post id=" + postId);
            return new PostNotFoundException("Post with id=" + postId + " not found");
        });

        Comment comment = Comment.builder()
                .content(content)
                .user(user)
                .post(post)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();
        commentRepo.save(comment);
        Logger.log(LoggerType.INFO, "Created comment id=" + comment.getId() + " on post id=" + postId);
        return comment;
    }

    public Comment createReplyToComment(String content, User user, UUID parentCommentId) {
        Comment parent = commentRepo.findById(parentCommentId).orElseThrow(() -> {
            Logger.log(LoggerType.FATAL, "Attempt to reply to nonexistent comment id=" + parentCommentId);
            return new CommentNotFoundException("Could not find comment with id=" + parentCommentId);
        });

        Comment reply = Comment.builder()
                .content(content)
                .user(user)
                .parentComment(parent)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();

        commentRepo.save(reply);
        Logger.log(LoggerType.INFO, "Created reply id=" + reply.getId() + " to comment id=" + parentCommentId);
        return reply;
    }

    public Comment updateComment(UUID id, String newContent, User user) {
        Comment existing = getComment(id);

        existing.setContent(newContent);
        existing.setUpdatedAt(LocalDateTime.now());

        try {
            commentRepo.save(existing);
            Logger.log(LoggerType.INFO, "Comment updated successfully!");
            return existing;
        } catch (Exception e) {
            Logger.log(LoggerType.FATAL, "Failed to update comment id=" + id);
            throw new RuntimeException("Failed to update comment");
        }
    }

    public void deleteComment(UUID id) {
        getComment(id);
        try {
            commentRepo.deleteById(id);
            Logger.log(LoggerType.INFO, "Comment id=" + id + " deleted successfully");
        } catch (Exception e) {
            Logger.log(LoggerType.FATAL, "Failed to delete comment id=" + id);
            throw new RuntimeException("Failed to delete comment with id=" + id);
        }
    }


    public List<Comment> getRepliesForComment(UUID commentId) {
        getComment(commentId);
        return commentRepo.findByParentCommentId(commentId);
    }

    public UUID findPostIdForComment(UUID commentId) {
        Optional<Comment> currentOptional = commentRepo.findById(commentId);

        while (currentOptional.isPresent()) {
            Comment current = currentOptional.get();
            if (current.getPost() != null) {
                return current.getPost().getId();
            }
            Comment parent = current.getParentComment();
            if (parent == null) break;
            currentOptional = commentRepo.findById(parent.getId());
        }

        throw new IllegalStateException("Could not trace postId for comment id=" + commentId);
    }



    public List<Comment> getTopLevelComments(UUID postId) {
        return commentRepo.findByPostId(postId).stream()
                .filter(c -> c.getParentComment() == null)
                .toList();
    }

    public int displayUpvotes(UUID id) {
        Comment comment = commentRepo.findById(id).orElseThrow();
        return voteRepo.countByCommentAndIsUpvote(comment, true);
    }

    public int displayDownvotes(UUID id) {
        Comment comment = commentRepo.findById(id).orElseThrow();
        return voteRepo.countByCommentAndIsUpvote(comment, false);
    }

}
