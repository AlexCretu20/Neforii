package handlers;
import client.CommentClient;
import config.ConnectionConfig;
import models.ApiResult;
import models.comment.CommentRequestDto;
import models.comment.CommentUpdateRequestDto;
import views.CommentView;

import java.util.*;

public class CommentHandler {
    private static final CommentClient commentClient = new CommentClient(ConnectionConfig.BASE_URL);
    private static Map<Integer, UUID> commentIndexMap = new HashMap<>();

    public static Map<Integer, UUID> getCommentIndexMap() {
        return commentIndexMap;
    }

    public static void loadComments(UUID postId) {
        ApiResult commentsResult = commentClient.getCommentsByPostId(postId);
        commentIndexMap = CommentView.displayCommentList(commentsResult);
    }

    public static void handleAddComment(Scanner scanner, UUID postId, UUID parentId) {
        if (UserHandler.currentUsername == null) {
            System.out.println("[ERROR]: You must be logged in to add a comment.");
            return;
        }

        System.out.print("Enter your comment: ");
        String content = scanner.nextLine();

        if (content.isBlank()) {
            System.out.println("[ERROR]: Comment cannot be empty.");
            return;
        }

        CommentRequestDto dto = new CommentRequestDto(content, UserHandler.currentUsername, parentId);
        ApiResult result = commentClient.addComment(postId, dto);

        if (result.getSuccess()) {
            System.out.println("[SUCCESS]: Comment added successfully.");
        } else {
            System.out.println("[ERROR]: " + result.getMessage());
        }
    }

    public static void handleReplyToComment(Scanner scanner, UUID postId) {
        System.out.print("Enter comment number to reply to: ");
        String indexStr = scanner.nextLine();
        try {
            int commentNumber = Integer.parseInt(indexStr);
            UUID parentId = commentIndexMap.get(commentNumber);

            if (parentId == null) {
                System.out.println("[ERROR]: No comment found with number: " + commentNumber);
                return;
            }

            handleAddComment(scanner, postId, parentId);
        } catch (NumberFormatException e) {
            System.out.println("[ERROR]: Please enter a valid number.");
        }
    }

    public static void handleEditComment(Scanner scanner) {
        if (commentIndexMap.isEmpty()) {
            System.out.println("[INFO]: Please open a post first to load its comments.");
            return;
        }

        System.out.print("Enter comment number to edit: ");
        String indexStr = scanner.nextLine();
        try {
            int commentNumber = Integer.parseInt(indexStr);
            UUID commentId = commentIndexMap.get(commentNumber);

            if (commentId == null) {
                System.out.println("[ERROR]: No comment found with number: " + commentNumber);
                return;
            }

            System.out.print("Enter new comment content: ");
            String newContent = scanner.nextLine();

            if (newContent.isBlank()) {
                System.out.println("[ERROR]: Content cannot be empty.");
                return;
            }

            CommentUpdateRequestDto updateDto = new CommentUpdateRequestDto(newContent);
            ApiResult result = commentClient.updateComment(commentId, updateDto);

            if (result.getSuccess()) {
                System.out.println("[SUCCESS]: Comment updated successfully.");
            } else {
                System.out.println("[ERROR]: " + result.getMessage());
            }

        } catch (NumberFormatException e) {
            System.out.println("[ERROR]: Please enter a valid number.");
        }
    }

    public static void handleDeleteComment(Scanner scanner) {
        if (commentIndexMap.isEmpty()) {
            System.out.println("[INFO]: Please open a post first to load its comments.");
            return;
        }

        System.out.print("Enter comment number to delete: ");
        String indexStr = scanner.nextLine();

        try {
            int commentNumber = Integer.parseInt(indexStr);
            UUID commentId = commentIndexMap.get(commentNumber);

            if (commentId == null) {
                System.out.println("[ERROR]: No comment found with number: " + commentNumber);
                return;
            }

            ApiResult deleteResult = commentClient.deleteComment(commentId);

            if (deleteResult.getSuccess()) {
                System.out.println("[SUCCESS]: Comment deleted successfully.");
                commentIndexMap.remove(commentNumber);
            } else {
                System.out.println("[ERROR]: " + deleteResult.getMessage());
            }

        } catch (NumberFormatException e) {
            System.out.println("[ERROR]: Please enter a valid number.");
        }
    }

    public static void handleVoteComment(Scanner scanner, String voteType) {
        if (commentIndexMap.isEmpty()) {
            System.out.println("[INFO]: Please open a post first to load its comments.");
            return;
        }

        System.out.print("Enter comment number to vote: ");
        String indexStr = scanner.nextLine();
        try {
            int commentNumber = Integer.parseInt(indexStr);
            UUID commentId = commentIndexMap.get(commentNumber);

            if (commentId == null) {
                System.out.println("[ERROR]: No comment found with number: " + commentNumber);
                return;
            }

            ApiResult result = commentClient.voteComment(commentId, voteType);

            if (result.getSuccess()) {
                System.out.println("[SUCCESS]: You voted the comment.");
            } else {
                System.out.println("[ERROR]: " + result.getMessage());
            }

        } catch (NumberFormatException e) {
            System.out.println("[ERROR]: Please enter a valid number.");
        }
    }
}
