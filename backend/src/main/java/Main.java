import service.UserService;

import java.util.Scanner;
public class Main {
    public static void main(String[] args) {
//        PostService postService = new PostService();
        UserService userService = UserService.getInstance();
        Scanner scanner = new Scanner(System.in);



        while (true) {
            System.out.println("Welcome to Neforii!\n");
            System.out.println("1. Register\n");
            System.out.println("2. Login\n");
            System.out.println("0. Exit\n");
            System.out.println("Enter your choice: ");
            String choice = scanner.nextLine();
            switch (choice) {
                case "1":
                    userService.registerUser();
                    break;

                case "2":
                    userService.loginUser();
                    break;

                case "0": {
                    System.out.println("Stopping the application!\n");
                    return;
                }
            }
            System.out.println("0. Logout\n");
            System.out.println("1. Creeaza o postare\n");
            System.out.printf("2. Vezi toate postarile\n");
            System.out.println("3. Sterge o postare\n");
            System.out.println("4. Vizualizeaza o postare\n");
            System.out.println("5. Comenteaza la o postare\n");
            // comenteaza la un comentariu
            // voteaza o postare
            // voteaza un comentariu
            // la 10 voturi pozitive ceva frumos
        }

    }
}
