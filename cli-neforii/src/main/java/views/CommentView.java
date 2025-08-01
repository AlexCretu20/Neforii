package views;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import models.ApiResult;

public class CommentView {
    private static final String GREEN = "\u001B[32m";
    private static final String NORMAL = "\u001B[0m";
    private static final String RED = "\u001B[31m";


private static void displayCommentWithReplies(JsonNode comment, int level) {
    String indent = "  ".repeat(level);
    String author = comment.get("author").asText();
    String content = comment.get("content").asText();
    System.out.println(indent + "- @" + author + ": " + content);

    JsonNode replies = comment.get("replies");
    if (replies != null && replies.isArray()) {
        for (JsonNode reply : replies) {
            displayCommentWithReplies(reply, level + 1);
        }
    }
}

    public static void displayCommentList(ApiResult apiResult) {
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
                    System.out.println("[INFO]: No comments found for this post.");
                }
            } catch (Exception e) {
                System.out.println(RED + "[ERROR]: Failed to display comments." + NORMAL);
                e.printStackTrace();
            }
        } else {
            System.out.println(RED + "[ERROR]: " + apiResult.getMessage() + NORMAL);
        }
    }

}
