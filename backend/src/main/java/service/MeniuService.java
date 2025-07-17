package service;

import exception.CommentNotFoundException;
import model.Post;
import ui.UserUI;

import java.util.Scanner;

public class MeniuService {
    private final CommentService commentService;
    private final PostService postService;
    private final UserService userService;
    private final VoteService voteService;
    private final Scanner scanner;
    private final UserUI userUI;

    public MeniuService(CommentService commentService, PostService postService,
                        UserService userService, VoteService voteService,
                        Scanner scanner, UserUI userUI) {
        this.commentService = commentService;
        this.postService = postService;
        this.userService = userService;
        this.voteService = voteService;
        this.scanner = scanner;
        this.userUI = userUI;
    }

    private void tryToLogin() {
        userUI.loginUserUI();
        if (userService.getCurrentUser() != null) {
            displayMainMeniu();
        }
    }

    public void displayLoginMeniu() {
        boolean flag = true;
        while (flag) {
            System.out.println("Welcome to Neforii!\n");
            if (userService.getCurrentUser() == null) {
                System.out.println("1. Register");
                System.out.println("2. Login");
                System.out.println("0. Exit");
                System.out.print("Enter your choice: ");
                String choice = scanner.nextLine();
                switch (choice) {
                    case "1" -> {
                        userUI.registerUserUI();
                        tryToLogin();
                    }
                    case "2" -> tryToLogin();
                    case "0" -> {
                        System.out.println("Stopping the application!\n");
                        flag = false;
                    }
                    default -> System.out.println("Invalid choice! Please try again!\n");
                }
            } else {
                System.out.println("1. Logout");
                System.out.println("0. Exit");
                System.out.print("Enter your choice: ");
                String choice = scanner.nextLine();
                switch (choice) {
                    case "1" -> {
                        String username = userService.getCurrentUser().getUsername();
                        userService.logoutUser();
                        System.out.println(username + " has logged out! See you next time!");
                    }
                    case "0" -> {
                        System.out.println("Stopping the application!\n");
                        flag = false;
                    }
                    default -> System.out.println("Invalid choice! Please try again!\n");
                }
            }
        }
    }

    public void displayMainMeniu() {
        boolean flag = true;

        while (flag) {
            System.out.println("\n--- Main Menu ---");
            System.out.println("1. Create a post");
            System.out.println("2. See all posts");
            System.out.println("3. Update a post");
            System.out.println("4. Delete a post");
            System.out.println("5. See a post");
            System.out.println("0. Back");
            System.out.print("Enter your choice: ");
            String choice = scanner.nextLine();

            switch (choice) {
                case "1" -> {
                    System.out.print("Enter your text for the post: ");
                    String text = scanner.nextLine();
                    postService.createPost(userService.getCurrentUser(), text);
                }
                case "2" -> postService.displayPosts();
                case "3" -> {
                    int postId = readValidPostId("Enter the ID of the post you want to modify: ");
                    Post post = postService.getPostById(postId);
                    if (!userService.getCurrentUser().equals(post.getUser())) {
                        System.out.println("You can't modify a post made by another user.");
                    } else {
                        System.out.print("Enter the new text: ");
                        String newText = scanner.nextLine();
                        postService.updatePost(postId, newText);
                    }
                }
                case "4" -> {
                    int postId = readValidPostId("Enter the ID of the post you want to delete: ");
                    Post post = postService.getPostById(postId);
                    if (!userService.getCurrentUser().equals(post.getUser())) {
                        System.out.println("You can't delete a post made by another user.");
                    } else {
                        postService.deletePost(postId);
                    }
                }
                case "5" -> {
                    int postId = readValidPostId("Enter the ID of the post to view: ");
                    postService.displayOnePost(postId);
                    displayPostMenu(postId);
                }
                case "0" -> flag = false;
                default -> System.out.println("Invalid option.");
            }
        }
    }

    public void displayPostMenu(int postId) {
        boolean flag = true;

        while (flag) {
            System.out.println("\n--- Post Menu ---");
            System.out.println("1. Show comments and votes");
            System.out.println("2. Leave a comment on this post");
            System.out.println("3. Upvote/downvote this post");
            System.out.println("4. Select a comment to interact with");
            System.out.println("0. Back to main menu");
            System.out.print("Enter your choice: ");
            String choice = scanner.nextLine();

            switch (choice) {
                case "1" -> commentService.showCommentsForPost(postId);
                case "2" -> {
                    System.out.print("Write your comment: ");
                    String text = scanner.nextLine();
                    commentService.createCommentOnPost(text, userService.getCurrentUser(), postId);
                }
                case "3" -> {
                    System.out.println("1. Upvote");
                    System.out.println("2. Downvote");
                    String voteChoice = scanner.nextLine();
                    if (voteChoice.equals("1") || voteChoice.equals("2")) {
                        boolean isUpvote = voteChoice.equals("1");
                        String result = voteService.createVote(userService.getCurrentUser().getId(), postId, null, isUpvote);
                        System.out.println(result);
                    }
                }
                case "4" -> {
                    int commentId = readValidCommentId("Enter the ID of the comment to interact with: ");
                    displayCommentMenu(commentId);
                }
                case "0" -> flag = false;
                default -> System.out.println("Invalid option.");
            }
        }
    }

    public void displayCommentMenu(int commentId) {
        boolean flag = true;

        while (flag) {
            System.out.println("\n--- Comment Menu ---");
            System.out.println("1. Reply to this comment");
            System.out.println("2. Upvote/downvote this comment");
            System.out.println("3. Show votes and replies to this comment");
            System.out.println("0. Back");
            System.out.print("Enter your choice: ");
            String choice = scanner.nextLine();

            switch (choice) {
                case "1" -> {
                    System.out.print("Write your reply: ");
                    String replyText = scanner.nextLine();
                    commentService.createReplyToComment(replyText, userService.getCurrentUser(), commentId);
                }
                case "2" -> {
                    System.out.println("1. Upvote");
                    System.out.println("2. Downvote");
                    String voteChoice = scanner.nextLine();
                    if (voteChoice.equals("1") || voteChoice.equals("2")) {
                        boolean isUpvote = voteChoice.equals("1");
                        String result = voteService.createVote(userService.getCurrentUser().getId(), null, commentId, isUpvote);
                        System.out.println(result);
                    }
                }
                case "3" -> commentService.showCommentsForComment(commentId);
                case "0" -> flag = false;
                default -> System.out.println("Invalid option.");
            }
        }
    }

    private int readValidPostId(String prompt) {
        int postId;
        while (true) {
            try {
                System.out.print(prompt);
                postId = Integer.parseInt(scanner.nextLine());
                if (postService.getPostById(postId) == null) {
                    System.out.println("No post found with that ID. Try again.");
                } else {
                    return postId;
                }
            } catch (NumberFormatException e) {
                System.out.println("Invalid number. Please enter a valid post ID.");
            }
        }
    }

    private int readValidCommentId(String prompt) {
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
}
