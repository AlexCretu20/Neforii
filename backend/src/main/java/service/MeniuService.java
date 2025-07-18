package service;

import ui.CommentUI;
import ui.PostUI;
import ui.UserUI;
import ui.VoteUI;

import java.util.Scanner;

public class MeniuService {
    private final CommentService commentService;
    private final PostService postService;
    private final UserService userService;
    private final VoteService voteService;
    private final Scanner scanner;
    private final UserUI userUI;
    private final PostUI postUI;
    private final CommentUI commentUI;
    private final VoteUI voteUI;

    public MeniuService(CommentService commentService, PostService postService,
                        UserService userService, VoteService voteService,
                        Scanner scanner, UserUI userUI, PostUI postUI, CommentUI commentUI, VoteUI voteUI) {
        this.commentService = commentService;
        this.postService = postService;
        this.userService = userService;
        this.voteService = voteService;
        this.scanner = scanner;
        this.userUI = userUI;
        this.postUI = postUI;
        this.commentUI = commentUI;
        this.voteUI = voteUI;
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
                case "1" -> postUI.createPostUI(userService.getCurrentUser());
                case "2" -> postUI.displayAllPostsUI();
                case "3" -> postUI.updatePostUI(userService.getCurrentUser());
                case "4" -> postUI.deletePostUI(userService.getCurrentUser());
                case "5" -> {
                    int postId = postUI.displayOnePostUI();
                    if (postService.getPostById(postId) != null) {
                        displayPostMenu(postId);
                    }
                }
                case "0" -> flag = false;
                default -> postUI.invalidOption();
            }
        }
    }

    public void displayPostMenu(int postId) {
        boolean flag = true;

        while (flag) {
            System.out.println("\n--- Post Menu ---");
            System.out.println("1. Show comments");
            System.out.println("2. Leave a comment on this post");
            System.out.println("3. Upvote/downvote this post");
            System.out.println("4. Select a comment to interact with");
            System.out.println("0. Back to main menu");
            System.out.print("Enter your choice: ");
            String choice = scanner.nextLine();

            switch (choice) {
                case "1" -> commentUI.showCommentsForPost(postId);
                case "2" -> commentUI.addCommentToPost(userService.getCurrentUser(), postId);
                case "3" -> {
                   voteUI.createVoteUI(userService.getCurrentUser(), postId);
                }
                case "4" -> {
                    int commentId = commentUI.readValidCommentId("Enter the ID of the comment to interact with: ");
                    commentUI.showRepliesForComment(commentId);
                    displayCommentMenu(commentId);
                }
                case "0" -> flag = false;
                default -> commentUI.invalidOption();
            }
        }
    }

    public void displayCommentMenu(int commentId) {
        boolean flag = true;

        while (flag) {
            System.out.println("\n--- Comment Menu ---");
            System.out.println("1. Reply to this comment");
            System.out.println("2. Reply to another reply");
            System.out.println("3. Upvote/downvote this comment");
            System.out.println("4. Show votes and replies to this comment");
            System.out.println("0. Back");
            System.out.print("Enter your choice: ");
            String choice = scanner.nextLine();

            switch (choice) {
                case "1" -> commentUI.replyToComment(userService.getCurrentUser(), commentId);
                case "2" -> {
                    int parentId = commentUI.readValidCommentId("Enter the ID of the comment you want to reply to: ");
                    commentUI.replyToComment(userService.getCurrentUser(), parentId);
                }
                case "3" -> {
                    voteUI.createVoteUI(userService.getCurrentUser(), commentId);
                }
                case "4" -> commentUI.showRepliesForComment(commentId);
                case "0" -> flag = false;
                default -> commentUI.invalidOption();
            }
        }
    }


}
