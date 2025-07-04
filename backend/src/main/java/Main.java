import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
//        PostService postService = new PostService();
        Scanner scanner = new Scanner(System.in);

        System.out.println("Welcome to Neforii!\n");
        System.out.println("1. Register\n");
        System.out.println("2. Login\n");
        System.out.println("0. Exit\n");
        String choice = scanner.nextLine();
        switch (choice) {
            case "1": {
                System.out.println("Register functionality is not implemented yet.\n");
            }
            case "2": {
                System.out.println("Login functionality is not implemented yet.\n");
                break;
            }
            case "0": {
                System.out.println("Stopping the application!\n");
                return;
            }
        }

        while (true) {
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
