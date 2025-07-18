package ui;

import exception.CommentNotFoundException;
import model.Comment;
import model.User;
import service.CommentService;

import java.util.List;
import java.util.Scanner;

public class CommentUI {
    private final Scanner scanner;
    private final CommentService commentService;

    public CommentUI(Scanner scanner, CommentService commentService) {
        this.scanner = scanner;
        this.commentService = commentService;
    }

    public void addCommentToPost(User user, int postId) {
        System.out.print("Write your comment: ");
        String text = scanner.nextLine();
        commentService.createCommentOnPost(text, user, postId);
        System.out.println("Comment added to the post with id: " + postId);
    }

    public void replyToComment(User user, int commentId) {
        System.out.print("Write your reply: ");
        String replyText = scanner.nextLine();
        commentService.createReplyToComment(replyText, user, commentId);
        System.out.println("Reply added to comment " + commentId);
    }

    public void showCommentsForPost(int postId) {
//        System.out.println("Upvotes: " + commentService.displayUpvotes(postId));
//        System.out.println("Downvotes: " + commentService.displayDownvotes(postId));
        List<Comment> allComments = commentService.getComments();

        List<Comment> topLevel = commentService.getTopLevelComments(postId);
        if (topLevel.isEmpty()) {
            System.out.println("This post has no comments yet.");
        } else {
            System.out.println("\n--- Comments ---");
            for (Comment c : topLevel) {
                displayCommentRecursive(allComments, c, 0);
            }
        }
    }

    public void showRepliesForComment(int commentId) {
        int upVotes = commentService.displayUpvotes(commentId);
        int downVotes = commentService.displayDownvotes(commentId);
        System.out.println("\nUpvotes: " + upVotes);
        System.out.println("Downvotes: " + downVotes);
        System.out.println("────────────────────────────");
        System.out.println("Replies:");

        List<Comment> all = commentService.getComments();
        List<Comment> rootReplies = commentService.getRepliesForComment(commentId);

        if (rootReplies.isEmpty()) {
            System.out.println("No replies to comment " + commentId + ".\n");
        } else {
            for (Comment reply : rootReplies) {
                displayCommentRecursive(all, reply, 1);
            }
        }
    }

    // Recursive method to display comments and their replies
    private void displayCommentRecursive(List<Comment> all, Comment comment, int level) {
        String indent = "  ".repeat(level);
        System.out.println(indent + comment.getId() + ". " + comment.getText() +
                " --- by " + comment.getUser().getUsername() + " at " + comment.getCreatedAt());
        System.out.println(indent + "Upvotes: " +
                commentService.displayUpvotes(comment.getId()) + ", Downvotes: " +
                commentService.displayDownvotes(comment.getId()));

        for (Comment reply : all) {
            if (reply.getParentCommentId() != null && reply.getParentCommentId().equals(comment.getId())) {
                displayCommentRecursive(all, reply, level + 1);
            }
        }
    }

    public int readValidCommentId(String prompt) {
        int commentId;
        while (true) {
            try {
                System.out.print(prompt);
                commentId = Integer.parseInt(scanner.nextLine());
                commentService.getComment(commentId);
                return commentId;
            } catch (NumberFormatException e) {
                System.out.println("Invalid number. Please enter a valid comment ID.");
            } catch (CommentNotFoundException e) {
                System.out.println("No comment found with that ID. Try again.");
            }
        }
    }

    public void invalidOption() {
        System.out.println("Invalid option.");
    }
}
