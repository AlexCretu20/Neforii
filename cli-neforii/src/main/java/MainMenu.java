import client.CommentClient;
import client.PostClient;
import client.UserClient;
import com.fasterxml.jackson.databind.ObjectMapper;
import config.ConnectionConfig;
import models.ApiResult;
import models.comment.CommentRequestDto;
import models.post.PostRequestDto;
import models.post.PostResponseDto;
import models.user.UserLoginRequestDto;
import models.user.UserRegisterRequestDto;
import models.user.UserResponseDto;
import views.CommentView;
import views.PostView;
import views.UserView;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;
import java.util.UUID;

public class MainMenu {
    private static final UserClient userClient = new UserClient(ConnectionConfig.BASE_URL + "/users");
    private static final PostClient postClient = new PostClient(ConnectionConfig.BASE_URL + "/posts");
//  private static final CommentClient commentClient = new CommentClient(ConnectionConfig.BASE_URL + "/comments");
    private static final CommentClient commentClient = new CommentClient(ConnectionConfig.BASE_URL);
    public static String currentUsername = null;
    public static UUID currentUserId = null;
    private static Map<Integer, UUID> commentIndexMap = new HashMap<>();

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);

        while (true) {

            System.out.println("====== Welcome to Neforii CLI ======");
            System.out.println("1. Login");
            System.out.println("2. Register");
            System.out.println("3. Exit");
            System.out.print("Choose option: ");
            String option = scanner.nextLine();

            switch (option) {
                case "1" -> handleLogin(scanner);
                case "2" -> handleRegister(scanner);
                case "3" -> {
                    System.out.println("Exiting... Goodbye!");
                    return;
                }
                default -> System.out.println("Invalid option. Please choose again.");
            }
        }
    }

    private static void postLoginMenu(Scanner scanner) {
        while (true) {

            System.out.println("\n==== Main Menu ====");
            System.out.println("1. Create Post");
            System.out.println("2. View Posts");
            System.out.println("3. Open Post (view comments)");
            System.out.println("4. Delete Post");
            System.out.println("5. Logout ");
            System.out.print("Choose option: ");
            String option = scanner.nextLine();

            switch (option) {
                case "1" -> handleCreatePost(scanner);
                case "2" -> handleViewPosts();
                case "3" -> handleOpenPost(scanner);
                case "5" -> {
                    currentUsername = null;
                    currentUserId = null;
                    return;
                }
                case "4" -> handleDeletePost(scanner);
                default -> System.out.println("Invalid option. Please try again.");
            }
        }
    }



    private static void handleLogin(Scanner scanner) {
        System.out.println("==== LOGIN ====");
        System.out.print("Email: ");
        String email = scanner.nextLine();

        System.out.print("Password: ");
        String password = scanner.nextLine();

        UserLoginRequestDto loginRequest = new UserLoginRequestDto(email, password);
        ApiResult result = userClient.login(loginRequest);

        UserView.displayUserResult(result);
        if (result.getSuccess()) {
            currentUsername = extractUsernameFromLogin(result);
            currentUserId = extractIdFromLogin(result);

            postLoginMenu(scanner);
        }
    }


private static void handleOpenPost(Scanner scanner) {
    if (postIndexMap.isEmpty()) {
        System.out.println("[INFO]: Please view posts first using option 2.");
        return;
    }

    System.out.print("Enter Post Number: ");
    String input = scanner.nextLine();

    try {
        int postNumber = Integer.parseInt(input);
        PostResponseDto selectedPost = postIndexMap.get(postNumber);

        if (selectedPost == null) {
            System.out.println("[ERROR]: No post found with number: " + postNumber);
            return;
        }


        ApiResult postResult = postClient.getPostById(selectedPost.id());
        PostView.displayPostResult(postResult, String.valueOf(postNumber));



        ApiResult commentsResult = postClient.getCommentsByPostId(selectedPost.id());
        commentIndexMap = CommentView.displayCommentList(commentsResult);


        while (true) {
            System.out.println("\n--- Comment Menu ---");
            System.out.println("1. Add a new comment");
            System.out.println("2. Reply to a comment");
            System.out.println("3. Back to main menu");
            System.out.print("Choose option: ");
            String choice = scanner.nextLine();

            switch (choice) {
                case "1" -> handleAddComment(scanner, selectedPost.id(), null);
                case "2" -> handleReplyToComment(scanner, selectedPost.id());
                case "3" -> { return; }
                default -> System.out.println("[ERROR]: Invalid choice.");
            }
        }

    } catch (NumberFormatException e) {
        System.out.println("[ERROR]: Please enter a valid number.");
    } catch (Exception e) {
        System.out.println("[ERROR]: Could not load post or comments.");
    }
}

    private static Map<Integer, PostResponseDto> postIndexMap = new HashMap<>();

    private static void handleViewPosts() {
        System.out.println("==== All Posts ====");
        ApiResult result = postClient.getAllPosts();
        postIndexMap = PostView.displayPostListResult(result);
    }

    private static void handleCreatePost(Scanner scanner) {
        if (currentUsername == null || currentUserId == null) {
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
                currentUsername,
                null,
                imagePath

        );

        ApiResult result = postClient.newPost(postDto);
       if(result.getSuccess()) {
           System.out.println("[INFO]: Post successfully created.");
       }
    }

    private static void handleRegister(Scanner scanner) {
        System.out.println("==== REGISTER ====");
        System.out.print("Username: ");
        String username = scanner.nextLine();

        System.out.print("Email: ");
        String email = scanner.nextLine();

        System.out.print("Password: ");
        String password = scanner.nextLine();

        System.out.print("Phone Number: ");
        String phoneNumber = scanner.nextLine();

        System.out.print("Description: ");
        String description = scanner.nextLine();

        UserRegisterRequestDto registerRequest = new UserRegisterRequestDto(username, email, password, phoneNumber, description);
        ApiResult result = userClient.register(registerRequest);

        System.out.println(result.getMessage());
        if (result.getSuccess()) {
            currentUsername = username;
            ApiResult userResult = userClient.getUserByUsername(username);
            UserView.displayUserResult(userResult);
            currentUserId = userClient.extractUserId(userResult);
            postLoginMenu(scanner);
        }
    }


    private static void handleDeletePost(Scanner scanner) {
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
        } catch (Exception e) {
            System.out.println("[ERROR]: Could not delete the post. " + e.getMessage());
        }
    }

    private static void handleAddComment(Scanner scanner, UUID postId, UUID parentId) {
        if (currentUsername == null) {
            System.out.println("[ERROR]: You must be logged in to add a comment.");
            return;
        }

        System.out.print("Enter your comment: ");
        String content = scanner.nextLine();

        if (content.isBlank()) {
            System.out.println("[ERROR]: Comment cannot be empty.");
            return;
        }

        CommentRequestDto dto = new CommentRequestDto(
                content,
                currentUsername,
                parentId
        );

        ApiResult result = commentClient.addComment(postId, dto);
        if (result.getSuccess()) {
            System.out.println("[SUCCESS]: Comment added successfully.");
        } else {
            System.out.println("[ERROR]: " + result.getMessage());
        }
    }


private static void handleReplyToComment(Scanner scanner, UUID postId) {
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


    private static String extractUsernameFromLogin(ApiResult loginResult) {
        try {
            ObjectMapper mapper = new ObjectMapper();
            mapper.findAndRegisterModules(); // pentru  datetime
            UserResponseDto user = mapper.readValue(loginResult.getResponseBody(), UserResponseDto.class);
            return user.username(); // getter pt username
        } catch (Exception e) {
            System.out.println("Couldn't parse login response to extract username.");
            return null;
        }
    }

    private static UUID extractIdFromLogin(ApiResult loginResult) {
        try {
            ObjectMapper mapper = new ObjectMapper();
            mapper.findAndRegisterModules();
            UserResponseDto user = mapper.readValue(loginResult.getResponseBody(), UserResponseDto.class);
            return user.id();
        } catch (Exception e) {
            System.out.println("Couldn't parse login response to extract id.");
            return null;
        }
    }


}