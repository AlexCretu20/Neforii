import client.CommentClient;
import client.PostClient;
import client.UserClient;
import com.fasterxml.jackson.databind.ObjectMapper;
import models.ApiResult;
import models.post.PostRequestDto;
import models.user.UserLoginRequestDto;
import models.user.UserRegisterRequestDto;
import models.user.UserResponseDto;
import views.CommentView;
import views.PostView;
import views.UserView;
import java.util.Scanner;
import java.util.UUID;

public class MainMenu {
    private static final String BASE_URL = "http://13.53.190.111:8080";
    private static final UserClient userClient = new UserClient(BASE_URL + "/users");
    private static final PostClient postClient = new PostClient(BASE_URL + "/posts");
    private static final CommentClient commentClient = new CommentClient(BASE_URL + "/comments");
    public static String currentUsername = null;
    public static UUID currentUserId = null;

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

    private static void handleLogin(Scanner scanner) {
        System.out.println("==== LOGIN ====");
        System.out.print("Email: ");
        String email = scanner.nextLine();

        System.out.print("Password: ");
        String password = scanner.nextLine();

        UserLoginRequestDto loginRequest = new UserLoginRequestDto(email, password);
        ApiResult result = userClient.login(loginRequest);

        System.out.println(result.getMessage());
        if (result.getSuccess()) {
            String username = extractUsernameFromLogin(result);
            if (username != null) {
                currentUsername = username;
                ApiResult userResult = userClient.getUserByUsername(username);
                UserView.displayUserResult(userResult);
                currentUserId = userClient.extractUserId(userResult);
                postLoginMenu(scanner);
            }
        }
    }

    private static void postLoginMenu(Scanner scanner) {
        while (true) {
            System.out.println("\n==== Main Menu ====");
            System.out.println("1. Create Post");
            System.out.println("2. View Posts");
            System.out.println("3. Open Post (view comments)");
            System.out.println("4. Logout");
            System.out.print("Choose option: ");
            String option = scanner.nextLine();

            switch (option) {
                case "1" -> handleCreatePost(scanner);
                case "2" -> handleViewPosts();
                case "3" -> handleOpenPost(scanner);
                case "4" -> {
                    currentUsername = null;
                    currentUserId = null;
                    return;
                }
                default -> System.out.println("Invalid option. Please try again.");
            }
        }
    }

private static void handleOpenPost(Scanner scanner) {
    System.out.print("Enter Post ID to view: ");
    String postIdInput = scanner.nextLine();
    try {
        UUID postId = UUID.fromString(postIdInput);
        ApiResult postResult = postClient.getPostById(postId);


        if (!postResult.getSuccess()) {
            System.out.println("[ERROR]: Could not load post: " + postResult.getMessage());
            return;
        }
        PostView.displayPostResult(postResult);

        ApiResult commentsResult = commentClient.getCommentsByPostId(postId);
        if (!commentsResult.getSuccess()) {
            System.out.println("[ERROR]: Could not load comments: " + commentsResult.getMessage());
            return;
        }
        CommentView.displayCommentList(commentsResult);

    } catch (IllegalArgumentException e) {
        System.out.println("[ERROR]: Invalid UUID format.");
    } catch (Exception e) {
        System.out.println("[ERROR]: Could not load post or comments.");
    }
}


    private static void handleViewPosts() {
        System.out.println("==== All Posts ====");
        ApiResult result = postClient.getAllPosts();
        PostView.displayPostListResult(result);
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
        PostView.displayPostResult(result);
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
}