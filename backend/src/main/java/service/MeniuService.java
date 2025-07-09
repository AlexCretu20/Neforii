package service;

import model.User;

import java.util.Scanner;

import static java.lang.Integer.parseInt;

public class MeniuService {
    private CommentService commentService;
    private PostService postService;
    private UserService userService;
    private VoteService voteService;
    private Scanner scanner;
    private User currentUser;

    public MeniuService(CommentService commentService, PostService postService, UserService userService, VoteService voteService, Scanner scanner) {
        this.commentService = commentService;
        this.postService = postService;
        this.userService = userService;
        this.voteService = voteService;
        this.scanner = scanner;

    }

    public void  displayLoginMeniu() {

        boolean flag = true;
        while (flag) {
            System.out.println("Welcome to Neforii!\n");
            System.out.println("1. Register\n");
            System.out.println("2. Login\n");
            System.out.println("0. Exit\n");
            System.out.println("Enter your choice: ");
            String choice = scanner.nextLine();
            switch (choice) {
                case "1":
                    userService.registerUser();
                    userService.loginUser();
                    displayMainMeniu();
                    break;

                case "2":
                    userService.loginUser();
                    this.currentUser = userService.getCurrentUser();
                    displayMainMeniu();
                    break;

                case "0": {
                    System.out.println("Stopping the application!\n");
                    flag = false;
                    break;

                }

            }
            System.out.println("0. Logout\n");
        }
    }

    public void displayMainMeniu(){
        boolean flag = true;

        while (flag){
            System.out.println("1. Create a post\n");
            System.out.println("2. See all posts\n");
            System.out.println("3. Delete a post\n");
            System.out.println("4. See a post\n");
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
                    // un user poate sterge doar o postare a lui
                    break;

                case "4":
                    System.out.println("Enter the number of a post:\n");
                    String number =  scanner.nextLine();
                    postService.ExpandComments(parseInt(number));
                    // new method
                    break;

                case "0":
                    flag = false;
                    break;
            }

        }
    }

}
