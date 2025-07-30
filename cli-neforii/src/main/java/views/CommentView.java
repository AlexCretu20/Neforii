package views;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import models.ApiResult;
import models.comment.CommentResponseDto;

import java.time.format.DateTimeFormatter;
import java.util.List;

public class CommentView {
    private static final String RED = "\u001B[31m";
    private static final String GREEN = "\u001B[32m";
    private static final String NORMAL = "\u001B[0m";

    private static void displayComment(CommentResponseDto comment, int indent) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
        String createdAtFormatted = comment.createdAt().format(formatter);

        String padding = " ".repeat(indent * 4);

        String[] fields = {
                "Author: @" + comment.author(),
                "Content: " + comment.content(),
                "Score: " + comment.score() + " (+" + comment.upVotes() + "/-" + comment.downVotes() + ")",
                "Created: " + createdAtFormatted,
                "ID: " + comment.id(),
                "ParentID: " + (comment.parentId() != null ? comment.parentId() : "None")
        };

        int maxLength = 32;
        for (String line : fields) {
            if (line.length() > maxLength) {
                maxLength = line.length();
            }
        }
        int widthPadding = maxLength + 2;

        String topBorder = padding + "┌" + "─".repeat(widthPadding) + "┐";
        String separator = padding + "├" + "─".repeat(widthPadding) + "┤";
        String bottomBorder = padding + "└" + "─".repeat(widthPadding) + "┘";

        System.out.println(GREEN + topBorder);
        System.out.printf(padding + "│ %-" + (widthPadding - 1) + "s│%n", fields[0]);
        System.out.println(separator);
        for (int i = 1; i < fields.length; i++) {
            System.out.printf(padding + "│ %-" + (widthPadding - 1) + "s│%n", fields[i]);
        }
        System.out.println(bottomBorder + NORMAL);

       //reply recursiv afisare
        List<CommentResponseDto> replies = comment.replies();
        if (replies != null && !replies.isEmpty()) {
            for (CommentResponseDto reply : replies) {
                displayComment(reply, indent + 1);
            }
        }
    }

    private static void displayError(String message) {
        String prefix = "[ERROR]: ";
        String fullMessage = prefix + message;

        int minWidth = 32;
        int width = Math.max(fullMessage.length(), minWidth) + 2;

        String topBorder = "┌" + "─".repeat(width) + "┐";
        String bottomBorder = "└" + "─".repeat(width) + "┘";

        System.out.println(RED + topBorder);
        System.out.printf("│ %-" + (width - 1) + "s│%n", fullMessage);
        System.out.println(bottomBorder + NORMAL);
    }

    public static void displayCommentResult(ApiResult apiResult) {
        if (apiResult.getSuccess()) {
            try {
                ObjectMapper objectMapper = new ObjectMapper();
                objectMapper.findAndRegisterModules();
                CommentResponseDto comment = objectMapper.readValue(apiResult.getResponseBody(), CommentResponseDto.class);
                displayComment(comment, 0);
            } catch (JsonProcessingException e) {
                displayError("A problem has appeared while processing comment data. Please try again later.");
            }
        } else {
            displayError(apiResult.getMessage());
        }
    }
    public static void displayCommentsResult(ApiResult apiResult) {
        if (apiResult.getSuccess()) {
            try {
                ObjectMapper objectMapper = new ObjectMapper();
                objectMapper.findAndRegisterModules();
                //lista de comentarii
                List<CommentResponseDto> comments = objectMapper.readValue(
                        apiResult.getResponseBody(),
                        objectMapper.getTypeFactory().constructCollectionType(List.class, CommentResponseDto.class)
                );

                if (comments.isEmpty()) {
                    System.out.println(GREEN + "[INFO] No comments found for this post." + NORMAL);
                } else {
                    for (CommentResponseDto comment : comments) {
                        displayComment(comment, 0);
                    }
                }
            } catch (JsonProcessingException e) {
                displayError("A problem has appeared while processing comments list. Please try again later.");
            }
        } else {
            displayError(apiResult.getMessage());
        }
    }

}
