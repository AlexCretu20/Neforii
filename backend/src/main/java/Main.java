import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
//        PostService postService = new PostService();
        Scanner scanner = new Scanner(System.in);

        System.out.println("Welcome to Neforii!");
        System.out.println("1. Register");
        System.out.println("2. Login");
        System.out.println("0. Exit");
        String choice = scanner.nextLine();
        switch (choice) {
            case "1": {
                System.out.println("Register functionality is not implemented yet.");
            }
            case "2": {
                System.out.println("Login functionality is not implemented yet.");
                break;
            }
            case "0": {
                System.out.println("Stopping the application!");
                return;
            }
        }

        while (true) {
            System.out.println("1. Create Post");
            break;
        }

    }
}
