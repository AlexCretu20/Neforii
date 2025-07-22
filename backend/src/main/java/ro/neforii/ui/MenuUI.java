package ro.neforii.ui;

import java.util.Scanner;

public class MenuUI {
    private final Scanner scanner;

    public MenuUI(Scanner scanner){
        this.scanner = scanner;
    }

    public String displayLoginMenu(boolean isLogged){
        System.out.println("\nWelcome to Neforii!\n");

        if (!isLogged) {
            System.out.println("1. Register");
            System.out.println("2. Login");
            System.out.println("0. Exit");
        } else {
            System.out.println("1. Logout");
            System.out.println("0. Exit");
        }

        System.out.print("Enter your choice: ");
        return scanner.nextLine();
    }

    public String displayMainMenu(){
        System.out.println("\n--- Main Menu ---");
        System.out.println("1. Create a post");
        System.out.println("2. See all posts");
        System.out.println("3. My Profile");
        System.out.println("4. See a post");
        System.out.println("0. Back");

        System.out.print("Enter your choice: ");
        return scanner.nextLine();
    }

    public String displayUserProfileMenu(){
        System.out.println("\n--- My Profile ---");
        System.out.println("1. My Details");
        System.out.println("2. My Posts");
        System.out.println("3. Update a post");
        System.out.println("4. Delete a post");
        System.out.println("5. Delete account");
        System.out.println("0. Back");

        System.out.print("Enter your choice: ");
        return scanner.nextLine();
    }

    public String displayPostMenu(){
        System.out.println("\n--- Post Menu ---");
        System.out.println("1. Show comments");
        System.out.println("2. Leave a comment on this post");
        System.out.println("3. Upvote/downvote this post");
        System.out.println("4. Select a comment to interact with");
        System.out.println("0. Back to main menu");
        System.out.print("Enter your choice: ");

        return scanner.nextLine();
    }

    public String displayCommentMenu(){
        System.out.println("\n--- Comment Menu ---");
        System.out.println("1. Reply to this comment");
        System.out.println("2. Reply to another reply");
        System.out.println("3. Upvote/downvote this comment");
        System.out.println("4. Show votes and replies to this comment");
        System.out.println("5. Update this comment");
        System.out.println("6. Delete this comment");
        System.out.println("0. Back");
        System.out.print("Enter your choice: ");
        return scanner.nextLine();
    }

    public void invalidOption(){
        System.out.println("Invalid option");
    }

    public void stopApplication(){
        System.out.println("Stopping the application...\n");
        System.out.println("See you next time!");
    }
}
