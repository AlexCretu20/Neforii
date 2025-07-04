import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
//        PostService postService = new PostService();
        Scanner scanner = new Scanner(System.in);

        while (true) {
            System.out.println("Welcome to Neforii!");
            System.out.println("1. Register");
            System.out.println("2. Login");
            System.out.println("0. Exit");
            String choice = scanner.nextLine();
            if (choice.equals("1")) {
                System.out.println("Registering a new user...");
            } else if (choice.equals("2")) {
                System.out.println("Logging in...");

            } else if (choice.equals("0")) {
                System.out.println("Exiting...");
                break;
            } else {
                System.out.println("Invalid choice, please try again.");
            }
        }

    }
}
