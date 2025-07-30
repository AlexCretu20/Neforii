package views;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import models.ApiResult;
import models.post.PostResponseDto;

import java.time.format.DateTimeFormatter;

public class PostView {
    private static final String RED = "\u001B[31m";
    private static final String GREEN = "\u001B[32m";
    private static final String NORMAL = "\u001B[0m";

    private static final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

    private static String[] wrapText(String text, int maxLineLength) {
        String[] words = text.split(" ");
        StringBuilder line = new StringBuilder();
        java.util.List<String> lines = new java.util.ArrayList<>();

        for (String word : words) {
            if (line.length() + word.length() + 1 > maxLineLength) {
                lines.add(line.toString());
                line = new StringBuilder(word);
            } else {
                if (line.length() > 0) {
                    line.append(" ");
                }
                line.append(word);
            }
        }
        if (line.length() > 0) {
            lines.add(line.toString());
        }

        return lines.toArray(new String[0]);
    }

    private static void displayPost(PostResponseDto post) {
        String createdAtFormatted = post.createdAt().format(formatter);

        int maxLineLength = 50;

        String[] wrappedTitle = wrapText("Title:     " + post.title(), maxLineLength);
        String[] wrappedAuthor = wrapText("Author:    @" + post.author(), maxLineLength);
        String[] wrappedContent = wrapText("Content:   " + post.content(), maxLineLength);
        String[] wrappedPostId = wrapText("Post ID:   " + post.id(), maxLineLength);
        String[] wrappedCreated = wrapText("Created:   " + createdAtFormatted, maxLineLength);

        java.util.List<String> allLines = new java.util.ArrayList<>();
        for (String s : wrappedTitle) allLines.add(s);
        for (String s : wrappedAuthor) allLines.add(s);
        for (String s : wrappedContent) allLines.add(s);
        for (String s : wrappedPostId) allLines.add(s);
        for (String s : wrappedCreated) allLines.add(s);

        int maxLength = 32;
        for (String line : allLines) {
            if (line.length() > maxLength) {
                maxLength = line.length();
            }
        }
        int widthPadding = maxLength + 2;

        String topBorder = "┌" + "─".repeat(widthPadding) + "┐";
        String separator = "├" + "─".repeat(widthPadding) + "┤";
        String bottomBorder = "└" + "─".repeat(widthPadding) + "┘";

        System.out.println(GREEN + topBorder);

        java.util.function.Consumer<String[]> printLines = lines -> {
            for (String line : lines) {
                System.out.printf("│ %-" + (widthPadding - 1) + "s│%n", line);
            }
        };

        printLines.accept(wrappedTitle);

        System.out.println(separator);

        printLines.accept(wrappedAuthor);
        printLines.accept(wrappedContent);
        printLines.accept(wrappedPostId);
        printLines.accept(wrappedCreated);

        System.out.println(bottomBorder + NORMAL);
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

    public static void displayPostResult(ApiResult apiResult) {
        if (apiResult.getSuccess()) {
            try {
                ObjectMapper objectMapper = new ObjectMapper();
                objectMapper.findAndRegisterModules();
                PostResponseDto post = objectMapper.readValue(apiResult.getResponseBody(), PostResponseDto.class);
                displayPost(post);
            } catch (JsonProcessingException e) {
                displayError("A problem has appeared while processing data. Please try again later.");
            }
        } else {
            displayError(apiResult.getMessage());
        }
    }

    public static void displayPostListResult(ApiResult apiResult) {
        if (apiResult.getSuccess()) {
            try {
                ObjectMapper objectMapper = new ObjectMapper();
                objectMapper.findAndRegisterModules();
                PostResponseDto[] posts = objectMapper.readValue(apiResult.getResponseBody(), PostResponseDto[].class);
                if (posts.length == 0) {
                    System.out.println(GREEN + "[INFO]: No posts available." + NORMAL);
                    return;
                }
                for (PostResponseDto post : posts) {
                    displayPost(post);
                    System.out.println();
                }
            } catch (JsonProcessingException e) {
                System.out.println(e.getMessage());
                displayError("A problem has appeared while processing data. Please try again later.");
            }
        } else {
            displayError(apiResult.getMessage());
        }
    }

}
