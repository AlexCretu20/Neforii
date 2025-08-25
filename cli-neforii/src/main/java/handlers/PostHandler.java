package handlers;

import client.PostClient;
import config.ConnectionConfig;
import models.ApiResult;
import models.post.PostRequestDto;
import models.post.PostResponseDto;
import models.post.PostUpdateRequestDto;
import views.PostView;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.*;

public class PostHandler {
    private static final PostClient postClient = new PostClient(ConnectionConfig.BASE_URL + "/posts");
    private static Map<Integer, PostResponseDto> postIndexMap = new HashMap<>();

    public static Map<Integer, PostResponseDto> getPostIndexMap() {
        return postIndexMap;
    }

    public static void handleViewPosts() {
        System.out.println("==== All Posts ====");
        ApiResult result = postClient.getAllPosts();
        postIndexMap = PostView.displayPostListResult(result);
    }

    public static void handleCreatePost(Scanner scanner) {
        if (UserHandler.currentUsername == null || UserHandler.currentUserId == null) {
            System.out.println("[ERROR]: You must be logged in to create a post.");
            return;
        }

        System.out.println("==== Create Post ====");
        System.out.print("Title: ");
        String title = scanner.nextLine();

        System.out.print("Content: ");
        String content = scanner.nextLine();

        System.out.print("Image path (optional, press Enter to skip): ");
        String imagePathInput = scanner.nextLine();
        String imagePath = imagePathInput.isBlank() ? null : imagePathInput;

        PostRequestDto postDto = new PostRequestDto(
                title,
                content,
                UserHandler.currentUsername,
                null
        );

        ApiResult result = postClient.newPost(postDto);
        if(result.getSuccess()) {
            System.out.println("[INFO]: Post successfully created.");
        }
    }

    public static void handleDeletePost(Scanner scanner) {
        if (postIndexMap.isEmpty()) {
            System.out.println("[INFO]: Please view posts first using option 2.");
            return;
        }

        System.out.print("Enter Post ID to delete: ");
        String input = scanner.nextLine();

        try {
            int postNumber = Integer.parseInt(input);
            PostResponseDto selectedPost = postIndexMap.get(postNumber);

            if (selectedPost == null) {
                System.out.println("[ERROR]: No post found with number: " + postNumber);
                return;
            }

            ApiResult result = postClient.deletePost(selectedPost.id());

            if (result.getSuccess()) {
                System.out.println("[SUCCESS]: Post deleted successfully.");
                postIndexMap.remove(postNumber);
            } else {
                System.out.println("[ERROR]: " + result.getMessage());
            }

        } catch (NumberFormatException e) {
            System.out.println("[ERROR]: Please enter a valid number.");
        }
    }

    public static void handleEditPost(Scanner scanner) {
        if (postIndexMap.isEmpty()) {
            System.out.println("[INFO]: Please view posts first using option 2.");
            return;
        }

        System.out.print("Enter Post Number to edit: ");
        String input = scanner.nextLine();

        try {
            int postNumber = Integer.parseInt(input);
            PostResponseDto selectedPost = postIndexMap.get(postNumber);

            if (selectedPost == null) {
                System.out.println("[ERROR]: No post found with number: " + postNumber);
                return;
            }

            System.out.println("Leave blank to keep current value.");
            System.out.print("New Title (current: " + selectedPost.title() + "): ");
            String newTitle = scanner.nextLine();
            if (newTitle.isBlank()) newTitle = null;

            System.out.print("New Content (current: " + selectedPost.content() + "): ");
            String newContent = scanner.nextLine();
            if (newContent.isBlank()) newContent = null;

            if (newTitle == null && newContent == null) {
                System.out.println("[INFO]: No changes entered. Post not updated.");
                return;
            }

            PostUpdateRequestDto updateDto = new PostUpdateRequestDto(newTitle, newContent);
            ApiResult result = postClient.updatePost(selectedPost.id(), updateDto);

            if(result.getSuccess()) {
                System.out.println("[SUCCESS]: Post updated successfully.");
            }else{
                System.out.println("[ERROR]: " + result.getMessage());
            }

        } catch (NumberFormatException e) {
            System.out.println("[ERROR]: Please enter a valid number.");
        }
    }

    public static void handleVotePost(UUID postId, String voteType) {
        ApiResult result = postClient.votePost(postId, voteType);

        if (result.getSuccess()) {
            System.out.println("[SUCCESS]: " + result.getMessage());

            try {
                ObjectMapper mapper = new ObjectMapper();
                JsonNode root = mapper.readTree(result.getResponseBody());
                JsonNode data = root.get("data");

                if (data != null && data.isObject()) {
                    int score = data.get("score").asInt();
                    String userVote = data.get("userVote").asText();
                    System.out.println("[INFO]: New total votes = " + score + " (your vote: " + userVote + ")");
                }
            } catch (Exception e) {
                System.out.println("[ERROR]: Couldn't parse vote response.");
            }

        } else {
            System.out.println("[ERROR]: " + result.getMessage());
        }
    }
}
