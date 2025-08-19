import handlers.UserHandler;
import handlers.PostHandler;
import handlers.CommentHandler;
import models.post.PostResponseDto;
import models.ApiResult;
import views.PostView;
import java.util.*;

public class MainMenu {
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
                case "1" -> UserHandler.handleLogin(scanner, () -> mainMenu(scanner));
                case "2" -> UserHandler.handleRegister(scanner, () -> mainMenu(scanner));
                case "3" -> {
                    System.out.println("Exiting... Goodbye!");
                    return;
                }
                default -> System.out.println("Invalid option. Please choose again.");
            }
        }
    }


    private static void mainMenu(Scanner scanner) {
        while (true) {
            System.out.println("\n==== Main Menu ====");
            System.out.println("1. Create Post");
            System.out.println("2. View Posts (and Post Menu)");
            System.out.println("3. Logout");
            System.out.print("Choose option: ");
            String option = scanner.nextLine();

            switch (option) {
                case "1" -> PostHandler.handleCreatePost(scanner);
                case "2" -> {
                    PostHandler.handleViewPosts();
                    postMenu(scanner);
                }
                case "3" -> {
                    UserHandler.currentUsername = null;
                    UserHandler.currentUserId = null;
                    System.out.println("[INFO] Logged out.");
                    return;
                }
                default -> System.out.println("Invalid option. Please try again.");
            }
        }
    }


    private static void postMenu(Scanner scanner) {
        while (true) {
            System.out.println("\n==== Post Menu ====");
            System.out.println("1. Upvote Post");
            System.out.println("2. Downvote Post");
            System.out.println("3. Open Post (and Comment Menu)");
            System.out.println("4. Edit Post");
            System.out.println("5. Delete Post");
            System.out.println("6. Create Post");
            System.out.println("7. Refresh Posts");
            System.out.println("8. Back to Main Menu");
            System.out.print("Choose option: ");
            String option = scanner.nextLine();

            switch (option) {
                case "1" -> votePost(scanner, "up");
                case "2" -> votePost(scanner, "down");
                case "3" -> handleOpenPost(scanner);
                case "4" -> PostHandler.handleEditPost(scanner);
                case "5" -> PostHandler.handleDeletePost(scanner);
                case "6" -> PostHandler.handleCreatePost(scanner);
                case "7" -> PostHandler.handleViewPosts();
                case "8" -> { return; }
                default -> System.out.println("Invalid option. Please try again.");
            }
        }
    }


    private static void commentMenu(Scanner scanner, UUID postId) {
        while (true) {
            System.out.println("\n==== Comment Menu ====");
            System.out.println("1. Upvote Comment");
            System.out.println("2. Downvote Comment");
            System.out.println("3. Add a New Comment");
            System.out.println("4. Reply to a Comment");
            System.out.println("5. Edit a Comment");
            System.out.println("6. Delete a Comment");
            System.out.println("7. Back to Post Menu");

            System.out.print("Choose option: ");
            String choice = scanner.nextLine();

            switch (choice) {
                case "1" -> CommentHandler.handleVoteComment(scanner, "up");
                case "2" -> CommentHandler.handleVoteComment(scanner, "down");
                case "3" -> CommentHandler.handleAddComment(scanner, postId, null);
                case "4" -> CommentHandler.handleReplyToComment(scanner, postId);
                case "5" -> CommentHandler.handleEditComment(scanner);
                case "6" -> CommentHandler.handleDeleteComment(scanner);
                case "7" -> { return; }

                default -> System.out.println("[ERROR]: Invalid choice.");
            }
        }
    }


    private static void handleOpenPost(Scanner scanner) {
        Map<Integer, PostResponseDto> postIndexMap = PostHandler.getPostIndexMap();
        if (postIndexMap.isEmpty()) {
            System.out.println("[INFO]: Please view posts first using option 7.");
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


            ApiResult postResult = new client.PostClient(config.ConnectionConfig.BASE_URL + "/posts")
                    .getPostById(selectedPost.id());
            PostView.displayPostResult(postResult, String.valueOf(postNumber));


            CommentHandler.loadComments(selectedPost.id());


            commentMenu(scanner, selectedPost.id());

        } catch (NumberFormatException e) {
            System.out.println("[ERROR]: Please enter a valid number.");
        }
    }

    private static void votePost(Scanner scanner, String type) {
        Map<Integer, PostResponseDto> postIndexMap = PostHandler.getPostIndexMap();
        if (postIndexMap.isEmpty()) {
            System.out.println("[INFO]: Please view posts first using option 7.");
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
            PostHandler.handleVotePost(selectedPost.id(), type);
        } catch (NumberFormatException e) {
            System.out.println("[ERROR]: Please enter a valid number.");
        }
    }
}
