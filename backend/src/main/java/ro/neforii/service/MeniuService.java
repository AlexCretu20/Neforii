package ro.neforii.service;

import ro.neforii.ui.*;

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
    private final MenuUI menuUI;

    public MeniuService(CommentService commentService, PostService postService,
                        UserService userService, VoteService voteService,
                        Scanner scanner, UserUI userUI, PostUI postUI, CommentUI commentUI, VoteUI voteUI, MenuUI menuUI) {
        this.commentService = commentService;
        this.postService = postService;
        this.userService = userService;
        this.voteService = voteService;
        this.scanner = scanner;
        this.userUI = userUI;
        this.postUI = postUI;
        this.commentUI = commentUI;
        this.voteUI = voteUI;
        this.menuUI = menuUI;
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
            boolean isLogged = userService.getCurrentUser()!=null;
            String choice = menuUI.displayLoginMenu(isLogged);
            if (!isLogged) {
                switch (choice) {
                    case "1" -> {
                        userUI.registerUserUI();
                        tryToLogin();
                    }
                    case "2" -> tryToLogin();
                    case "0" -> {
                        menuUI.stopApplication();
                        flag = false;
                    }
                    default -> menuUI.invalidOption();
                }
            } else {
                switch (choice) {
                    case "1" -> userUI.logoutUserUI();
                    case "0" -> {
                        menuUI.stopApplication();
                        flag = false;
                    }
                    default -> menuUI.invalidOption();
                }
            }
        }
    }

    public void displayMainMeniu() {
        boolean flag = true;

        while (flag) {
            String choice = menuUI.displayMainMenu();

            switch (choice) {
                case "1" -> postUI.createPostUI(userService.getCurrentUser());
                case "2" -> postUI.displayAllPostsUI();
                case "3" -> displayProfileMenu();
                case "4" -> {
                    int postId = postUI.displayOnePostUI();
                    if (postService.getPostById(postId) != null) {
                        displayPostMenu(postId);
                    }
                }
                case "0" -> flag = false;
                default -> menuUI.invalidOption();
            }
        }
    }

    public void displayProfileMenu(){
        boolean flag = true;

        while(flag){
            String choice=menuUI.displayUserProfileMenu();

            switch(choice){
                case "1" -> userUI.displayUserProfile(userService.getCurrentUser());  //e gata
                case "2" -> postUI.displayPostsForUser(userService.getCurrentUser().getId()); //mai trebuie adaugate comentariile pentru o postare
                case "3" -> postUI.updatePostUI(userService.getCurrentUser());  //e gata
                case "4" -> postUI.deletePostUI(userService.getCurrentUser());  //e gata
                case "5" -> userUI.deleteUser(userService.getCurrentUser());//e gata
                case "0" -> flag = false;
                default -> menuUI.invalidOption();
            }
        }

    }

    public void displayPostMenu(int postId) {
        boolean flag = true;

        while (flag) {
            String choice = menuUI.displayPostMenu();

            switch (choice) {
                case "1" -> commentUI.showCommentsForPost(postId);
                case "2" -> commentUI.addCommentToPost(userService.getCurrentUser(), postId);
                case "3" -> {
                   voteUI.createVoteUiPost(userService.getCurrentUser(), postId);
                }
                case "4" -> {
                    int commentId = commentUI.readValidCommentId("Enter the ID of the comment to interact with: ");
                    if (commentUI.checkPoostId(postId, commentId)){
                        commentUI.checkPoostId(postId, commentId);
                        displayCommentMenu(commentId, postId);
                    }
                }
                case "0" -> flag = false;
                default -> menuUI.invalidOption();
            }
        }
    }

    public void displayCommentMenu(int commentId, int postId) {
        boolean flag = true;

        while (flag) {
            String choice = menuUI.displayCommentMenu();

            switch (choice) {
                case "1" -> commentUI.replyToComment(userService.getCurrentUser(), commentId, postId);
                case "2" -> {
                    int parentId = commentUI.readValidCommentId("Enter the ID of the comment you want to reply to: ");
                    commentUI.replyToComment(userService.getCurrentUser(), parentId, postId);
                }
                case "3" -> {
                    voteUI.createVoteUiComment(userService.getCurrentUser(), commentId);
                }
                case "4" -> commentUI.showRepliesForComment(commentId);
                case "5" -> commentUI.updateComment(userService.getCurrentUser(),commentId);
                case "6" -> commentUI.deleteComment(userService.getCurrentUser(),commentId);
                case "0" -> flag = false;
                default -> menuUI.invalidOption();
            }
        }
    }


}
