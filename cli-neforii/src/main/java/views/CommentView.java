package views;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import models.ApiResult;

import java.util.*;

public class CommentView {
    private static final String RED = "\u001B[31m";
    private static final String GREEN = "\u001B[32m";
    private static final String NORMAL = "\u001B[0m";

    private static int globalIndex;
    private static Map<Integer, UUID> indexMap;

    private static String[] wrapText(String text, int maxLineLength) {
        if (text == null) return new String[]{""};
        String[] words = text.split(" ");
        StringBuilder line = new StringBuilder();
        List<String> lines = new ArrayList<>();

        for (String word : words) {
            if (line.length() + word.length() + 1 > maxLineLength) {
                lines.add(line.toString());
                line = new StringBuilder(word);
            } else {
                if (line.length() > 0) line.append(" ");
                line.append(word);
            }
        }
        if (line.length() > 0) {
            lines.add(line.toString());
        }
        return lines.toArray(new String[0]);
    }

    private static String formatTimestamp(String rawTimestamp) {
        if (rawTimestamp == null || rawTimestamp.isEmpty()) return "N/A";
        try {
            return rawTimestamp.replace('T', ' ').split("\\.")[0];
        } catch (Exception e) {
            return rawTimestamp;
        }
    }

private static void displayCommentBox(JsonNode comment, int level, int commentIndex) {
    int maxLineLength = 45; // lungimea max a liniei textului în box
    String indent = "  ".repeat(level);

    String author = comment.has("author") ? comment.get("author").asText() : "unknown";
    String content = comment.has("content") ? comment.get("content").asText() : "";
    int upVotes = comment.has("upVotes") ? comment.get("upVotes").asInt() : 0;
    int downVotes = comment.has("downVotes") ? comment.get("downVotes").asInt() : 0;
    String createdAt = comment.has("createdAt") ? formatTimestamp(comment.get("createdAt").asText()) : "N/A";

    // Linia author cu ComId
    String authorLine = String.format("Author: @%-12s ComId: %d", author, commentIndex);
    String[] wrappedAuthor = wrapText(authorLine, maxLineLength);
    String[] wrappedContent = wrapText("Content: " + content, maxLineLength);
    String votesLine = "Upvotes: " + upVotes + "   Downvotes: " + downVotes;
    String[] wrappedVotes = wrapText(votesLine, maxLineLength);
    String[] wrappedCreated = wrapText("Created: " + createdAt, maxLineLength);

    List<String> allLines = new ArrayList<>();
    for (String s : wrappedAuthor) allLines.add(s);
    for (String s : wrappedContent) allLines.add(s);
    for (String s : wrappedVotes) allLines.add(s);
    for (String s : wrappedCreated) allLines.add(s);

    int maxLength = 0;
    for (String line : allLines) {
        if (line.length() > maxLength) maxLength = line.length();
    }
    int widthPadding = maxLength + 2;

    String topBorder = indent + "┌" + "─".repeat(widthPadding) + "┐";
    String bottomBorder = indent + "└" + "─".repeat(widthPadding) + "┘";

    System.out.println(GREEN + topBorder);
    for (String line : allLines) {
        System.out.printf(indent + "│ %-" + (widthPadding - 1) + "s│%n", line);
    }
    System.out.println(bottomBorder + NORMAL);
}

private static void displayCommentWithReplies(JsonNode comment, int level) {
    UUID commentId = UUID.fromString(comment.get("id").asText());
    int currentIndex = globalIndex++;
    indexMap.put(currentIndex, commentId);

    displayCommentBox(comment, level, currentIndex);

    JsonNode replies = comment.get("replies");
    if (replies != null && replies.isArray()) {
        for (JsonNode reply : replies) {
            displayCommentWithReplies(reply, level + 1);
        }
    }
}


public static Map<Integer, UUID> displayCommentList(ApiResult apiResult) {
    indexMap = new HashMap<>();
    globalIndex = 1;

    if (apiResult.getSuccess()) {
        try {
            ObjectMapper mapper = new ObjectMapper();
            JsonNode root = mapper.readTree(apiResult.getResponseBody());
            JsonNode dataNode = root.get("data");

            if (dataNode != null && dataNode.isArray() && dataNode.size() > 0) {
                for (JsonNode comment : dataNode) {
                    displayCommentWithReplies(comment, 0);
                }
            } else {
                System.out.println(GREEN + "[INFO]: No comments found for this post." + NORMAL);
            }
        } catch (Exception e) {
            System.out.println(RED + "[ERROR]: Failed to display comments." + NORMAL);
            e.printStackTrace();
        }
    } else {
        System.out.println(RED + "[ERROR]: " + apiResult.getMessage() + NORMAL);
    }
    return indexMap;
}

}
