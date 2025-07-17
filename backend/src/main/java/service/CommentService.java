package service;

import exception.CommentNotFoundException;
import exception.PostNotFoundException;
import model.Comment;
import model.Post;
import model.User;
import repository.CommentRepository;
import repository.PostRepository;
import repository.UserRepository;
import repository.VoteRepository;
import utils.logger.Logger;
import utils.logger.LoggerType;

import java.util.List;
import java.util.Optional;

public class CommentService implements IVotable {
    private CommentRepository commentRepo;
    private UserRepository userRepo;
    private PostRepository postRepo;
    private VoteRepository voteRepo;

    public CommentService(CommentRepository commentRepo, UserRepository userRepo, PostRepository postRepo, VoteRepository voteRepo) {
        this.commentRepo = commentRepo;
        this.userRepo = userRepo;
        this.postRepo = postRepo;
        this.voteRepo = voteRepo;
    }

    //all existing comments
    public List<Comment> getComments() {
        return commentRepo.findAll();
    }

    public Comment getComment(int id) {
        Optional<Comment> comment = commentRepo.findById(id);
        if (comment.isEmpty()) {
            Logger.log(LoggerType.FATAL, "Comment with id=" + id + " not found in getComment()");
            throw new CommentNotFoundException("The comment with id " + id + "does not exist");
        }
        Logger.log(LoggerType.INFO, "Fetched comment with id=" + id);
        return comment.get();
    }

    public void createCommentOnPost(String text, User user, int postId) {
        if (postRepo.findById(postId).isEmpty()) {
            Logger.log(LoggerType.FATAL, "Attempt to comment on nonexistent post id=" + postId);
            throw new PostNotFoundException("Post with id=" + postId + " not found");
        }
        Comment comment = new Comment(text, user, postId, null);
        commentRepo.save(comment);
        Logger.log(LoggerType.INFO, "Created comment id=" + comment.getId() + " on post id=" + postId);
        System.out.println("Comment added to post " + postId + " with id=" + comment.getId());
    }

    public void createReplyToComment(String text, User user, int parentCommentId) {
        if (commentRepo.findById(parentCommentId).isEmpty()) {
            Logger.log(LoggerType.FATAL, "Attempt to reply to nonexistent comment id=" + parentCommentId);
            throw new CommentNotFoundException("Could not find comment with id=" + parentCommentId);
        }
        Comment reply = new Comment(text, user, null, parentCommentId);
        commentRepo.save(reply);
        Logger.log(LoggerType.INFO, "Created reply id=" + reply.getId() + " to comment id=" + parentCommentId);
        System.out.println("Reply added to comment " + parentCommentId + " with id=" + reply.getId());
    }

    //se paseaza updatedComment ca parametru, repositoryul obtine commentul si ii inlocuieste field-urile cu commentul din acest parametru
    public void updateComment(int id, Comment comment) {
        Optional<Comment> commentOptional = commentRepo.findById(id);
        if (commentOptional.isEmpty()) {
            Logger.log(LoggerType.FATAL, "Attempt to update nonexistent comment id=" + id);
            throw new CommentNotFoundException("The comment with id " + id + "does not exist");
        }
        Comment commentToUpdate = commentOptional.get();
        try {
            commentRepo.update(commentToUpdate);
            Logger.log(LoggerType.INFO, "Comment upgrated successfully!");
        } catch (Exception e) {
            Logger.log(LoggerType.FATAL, "Failed to update comment id=" + id);
        }
    }

    public void deleteComment(int id) {
        Optional<Comment> commentOptional = commentRepo.findById(id);
        if (commentOptional.isEmpty()) {
            Logger.log(LoggerType.FATAL, "Attempt to delete nonexistent comment id=" + id);
            throw new CommentNotFoundException("The comment with id " + id + "does not exist");
        }
        try {
            commentRepo.deleteById(id);
            Logger.log(LoggerType.INFO, "Comment id=" + id + " deleted successfully");
        } catch (Exception e) {
            Logger.log(LoggerType.FATAL, "Failed to delete comment id=" + id);
        }
    }

    public void showCommentsForPost(int postId) {
        Optional<Post> postOptional = postRepo.findById(postId);
        if (postOptional.isEmpty()) {
            Logger.log(LoggerType.FATAL, "Attempt to list comments for nonexistent post id=" + postId);
            throw new PostNotFoundException("Could not find post with id=" + postId);
        }

        Post post = postOptional.get();

        System.out.println(post);
        System.out.println("Upvotes: " + voteRepo.countVotesByPostId(postId, true));
        System.out.println("Downvotes: " + voteRepo.countVotesByPostId(postId, false));

        List<Comment> comments = commentRepo.findByPostId(postId);
        if (comments.isEmpty()) {
            System.out.println("This post has no comments yet.\n");
        } else {
            System.out.println("Comments:");
            for (Comment comment : comments) {
                System.out.println(comment.getId() + ". " + comment.getText() + " --- by " + comment.getUser().getUsername() +" at " + comment.getCreatedAt());
                System.out.println("Upvotes : " + displayUpvotes(comment.getId()) + " Downvotes : " + displayDownvotes(comment.getId()));
            }
        }
    }

    public void showCommentsForComment(int commentId) {
        System.out.println("Upvotes : " + displayUpvotes(commentId));
        System.out.println("Downvotes : " + displayDownvotes(commentId));
        System.out.println("────────────────────────────");
        System.out.println("Replies");
        Optional<Comment> commentOptional = commentRepo.findById(commentId);
        if (commentOptional.isEmpty()) {
            Logger.log(LoggerType.FATAL, "Attempt to list replies for nonexistent comment id=" + commentId);
            throw new CommentNotFoundException("Could not find comment with id=" + commentId);
        }
        List<Comment> replies = commentRepo.findByCommentId(commentId);
        if (replies.isEmpty()) {
            Logger.log(LoggerType.INFO, "No replies found for comment id=" + commentId);
            System.out.println("No replies to comment " + commentId + ".\n");
        } else {
            Logger.log(LoggerType.INFO, "Displaying " + replies.size() + " replies for comment id=" + commentId);
            for (Comment reply : replies) {
                System.out.println(reply.getId() + ". " + reply.getText() + " ---   by " + reply.getUser().getUsername() + "  posted at " + reply.getCreatedAt());
            }
        }
    }


    public int displayUpvotes(int id) {
        return voteRepo.countVotesByCommentId(id, true);
    }

    public int displayDownvotes(int id) {
        return voteRepo.countVotesByCommentId(id, false);
    }
}
