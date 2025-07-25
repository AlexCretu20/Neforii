package ro.neforii.ui;

import ro.neforii.exception.CommentNotFoundException;
import ro.neforii.model.Comment;
import ro.neforii.model.User;
import ro.neforii.service.CommentService;

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

    public void replyToComment(User user, int commentId, int postId) {
        if (postId == commentService.findPostIdForComment(commentId)) {
            System.out.print("Write your reply: ");
            String replyText = scanner.nextLine();
            commentService.createReplyToComment(replyText, user, commentId);
            System.out.println("Reply added to comment " + commentId);
        } else {
            System.out.println("The comment doesn't belog to post with id " + postId);
        }

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
        Comment comment = commentService.getComment(commentId);

        System.out.println("────────────────────────────");
        System.out.println(comment.getId() + ". " + comment.getContent() +
                " --- by " + comment.getUser().getUsername() + " at " + comment.getCreatedAt());
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
        System.out.println(indent + comment.getId() + ". " + comment.getContent() +
                " --- by " + comment.getUser().getUsername() + " at " + comment.getCreatedAt());
        System.out.println(indent + "Upvotes: " +
                commentService.displayUpvotes(comment.getId()) + ", Downvotes: " +
                commentService.displayDownvotes(comment.getId()));

//        for (Comment reply : all) {
//            if (reply.getParentCommentId() != null && reply.getParentCommentId().equals(comment.getId())) {
//                displayCommentRecursive(all, reply, level + 1);
//            }
//        }
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


    public void updateComment(User user,int commentId){
        Comment originalComment = commentService.getComment(commentId);

        if(user.getId()!=originalComment.getUser().getId()){
            System.out.println("You are not allowed to edit someone else's comment.");
            return;
        }

        System.out.println("Previous text: " + originalComment.getContent());
        System.out.println("Enter the new text (leave blank to cancel):");
        String text = scanner.nextLine().trim();

        if (text.isEmpty()) {
            System.out.println("Update canceled.");
            return;
        }

        commentService.updateComment(commentId,text,user);
        System.out.println("Comment with id "+commentId+" updated successfully!");
    }

    public void deleteComment(User user,int commentId){
        Comment originalComment = commentService.getComment(commentId);

        if(user.getId()!=originalComment.getUser().getId()){
            System.out.println("You are not allowed to delete someone else's comment.");
            return;
        }

        System.out.print("Are you sure you want to delete this comment? (y/n): ");
        String confirmation = scanner.nextLine().trim().toLowerCase();
        if (!confirmation.equals("y")) {
            System.out.println("Deletion canceled.");
            return;
        }

        commentService.deleteComment(commentId);
        System.out.println("Comment with id "+commentId+" deleted successfully!");
    }

    public boolean checkPoostId(int postId, int commentId) {
        if (postId == commentService.findPostIdForComment(commentId)) {
           return true;
        } else {
            System.out.println("The comment doesn't belog to post with id " + postId);
            return false;
        }

    }
}
