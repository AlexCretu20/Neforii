package service;

import model.EntityType;
import model.User;
import ui.UserUI;

import java.util.Scanner;

public class MeniuService {
    private CommentService commentService;
    private PostService postService;
    private UserService userService;
    private VoteService voteService;
    private Scanner scanner;
    private User currentUser;
    private UserUI userUI;

    public MeniuService(CommentService commentService, PostService postService, UserService userService, VoteService voteService, Scanner scanner, UserUI userUI) {
        this.commentService = commentService;
        this.postService = postService;
        this.userService = userService;
        this.voteService = voteService;
        this.scanner = scanner;
        this.userUI = userUI;

    }

    private void tryToLogin(){
        userUI.loginUserUI();
        this.currentUser = userService.getCurrentUser();
        if (this.currentUser != null) {
            displayMainMeniu();
        }
    }

    public void  displayLoginMeniu() {

        boolean flag = true;
        while (flag) {
            System.out.println("Welcome to Neforii!\n");
            System.out.println("1. Register");
            System.out.println("2. Login");
            System.out.println("0. Exit");
            System.out.println("Enter your choice: ");
            String choice = scanner.nextLine();
            switch (choice) {
                case "1":
                    userUI.registerUserUI();
                    tryToLogin();
                    break;

                case "2":
                    tryToLogin();
                    break;

                case "0":
                    System.out.println("Stopping the application!\n");
                    flag = false;
                    break;
                default:
                    System.out.println("Invalid choice! Please try again!\n");

            }
        }
    }

    public void displayMainMeniu(){
        boolean flag = true;

        while (flag){
            System.out.println("1. Create a post\n");
            System.out.println("2. See all posts\n");
            //daca un user are postari atunci ii apare in meniu optiunea de editarea/stergerea a postarii
            System.out.println("3. Update a post\n");
            System.out.println("4. Delete a post\n");
            System.out.println("5. See a post\n");
            System.out.println("0. Back\n");
            System.out.println("Enter your choice: ");
            String choice = scanner.nextLine();


            switch (choice){

                case "1":
                    System.out.println("Enter your text for the post:");
                    String text = scanner.nextLine();
                    postService.CreatePost(this.currentUser, text);
                    break;

                case "2":
                    postService.DisplayPosts();
                    break;

                case "3":
                    // un user poate modifica doar o postare a lui
                    System.out.println("Enter the id of the post you want to modify");
                    int number =  scanner.nextInt();
                    scanner.nextLine();
                    if ( currentUser != postService.getPostById(number).getUser()){
                        System.out.println("You can't modify a post made by another user ");
                    }
                    else {
                        System.out.println("Enter the new text: ");
                        String newText = scanner.nextLine();
                        postService.UpdatePost(number, newText);
                    }
                    break;

                case "4":
                    // un user poate sterge doar o postare a lui
                    System.out.println("Enter the id of the post you want to modify");
                    int number2 =  scanner.nextInt();
                    scanner.nextLine();
                    if ( currentUser != postService.getPostById(number2).getUser()){
                        System.out.println("You can't delete a post made by another user ");
                    }
                    else {
                        postService.DeletePost(number2);
                    }

                case "5":
                    System.out.println("Enter the number of a post:\n");
                    int number3 =  scanner.nextInt();
                    scanner.nextLine();
                    //sa se afiseze numarul de upvotes, downvotes, comenatrii
                    postService.displayOnePost(number3);
                    displayPostMenu(number3);
                    break;

                case "0":
                    flag = false;
                    break;
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
                case "1":
                    postService.ExpandComments(postId); // afișare recursivă comentarii
                    break;

                case "2":
                    System.out.print("Write your comment: ");
                    String text = scanner.nextLine();
                    commentService.createComment(text, currentUser, EntityType.POST, postId);
                    break;

                case "3":
                    if (voteService.hasUserVoted(currentUser.getId(), EntityType.POST, postId))
                        System.out.println("The user already voted");
                    else {
                        System.out.println("1. Upvote");
                        System.out.println("2. Downvote");
                        String voteChoice = scanner.nextLine();
                        if (voteChoice.equals("1") || voteChoice.equals("2")) {
                            boolean isUpvote = voteChoice.equals("1");
                            String result = voteService.createVote(currentUser.getId(), EntityType.POST, postId, isUpvote);
                            System.out.println(result);
                        }
                    }
                    break;

                case "4":
                    System.out.print("Enter the ID of the comment to interact with: ");
                    int commentId = Integer.parseInt(scanner.nextLine());
                    commentService.getComment(commentId);
                    displayCommentMenu(commentId); // meniul pentru comentariu selectat
                    break;

                case "0":
                    flag = false;
                    break;

                default:
                    System.out.println("Invalid option.");
            }
        }
    }



    public void displayCommentMenu(int commentId) {
        boolean flag = true;

        while (flag) {
            System.out.println("\n--- Comment Menu ---");
            System.out.println("1. Reply to this comment");
            System.out.println("2. Upvote/downvote this comment");
            System.out.println("3. Show replies to this comment");
            System.out.println("0. Back");
            System.out.print("Enter your choice: ");
            String choice = scanner.nextLine();

            switch (choice) {
                case "1":
                    System.out.print("Write your reply: ");
                    String replyText = scanner.nextLine();
                    commentService.createComment(replyText, currentUser, EntityType.COMMENT, commentId);
                    break;

                case "2":
                    if (voteService.hasUserVoted(currentUser.getId(), EntityType.COMMENT, commentId))
                        System.out.println("The user already voted");
                    else {
                        System.out.println("1. Upvote");
                        System.out.println("2. Downvote");
                        String voteChoice = scanner.nextLine();
                        if (voteChoice.equals("1") || voteChoice.equals("2")) {
                            boolean isUpvote = voteChoice.equals("1");
                            String result = voteService.createVote(currentUser.getId(), EntityType.COMMENT, commentId, isUpvote);
                            System.out.println(result);
                        }
                    }
                    break;

                case "3":
                    commentService.showReplies(commentId);
                    break;

                case "0":
                    flag = false;
                    break;

                default:
                    System.out.println("Invalid option.");
            }
        }
    }

}
