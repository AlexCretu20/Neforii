import client.CommentClient;
import client.PostClient;
import client.UserClient;
import com.fasterxml.jackson.databind.ObjectMapper;
import config.ConnectionConfig;
import models.ApiResult;
import models.user.UserLoginRequestDto;
import models.user.UserRegisterRequestDto;
import models.user.UserResponseDto;
import views.UserView;

import java.util.Scanner;


public class MainMenu {
    private static final UserClient userClient = new UserClient(ConnectionConfig.BASE_URL + "/users");
    private static final PostClient postClient = new PostClient(ConnectionConfig.BASE_URL + "/posts");
    private static final CommentClient commentClient = new CommentClient(ConnectionConfig.BASE_URL + "/comments");
    public static String currentUsername = null;

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);

        while (true) {
            System.out.println("1. Login");
            System.out.println("2. Register");
            System.out.println("3. Exit");
            System.out.print("Choose option: ");
            String option = scanner.nextLine();

            switch (option) {
                case "1" -> handleLogin(scanner);
                case "2" -> handleRegister(scanner);
                case "3" -> {
                    System.out.println("Goodbye!");
                    return;
                }
                default -> System.out.println("Invalid option. Please choose again.");
            }
        }
    }

    private static void handleLogin(Scanner scanner) {
        System.out.println("==== LOGIN ====");
        System.out.print("Email: ");
        String email = scanner.nextLine();

        System.out.print("Password: ");
        String password = scanner.nextLine();

        UserLoginRequestDto loginRequest = new UserLoginRequestDto(email, password);
        ApiResult result = userClient.login(loginRequest);

        System.out.println(result.getMessage());
        if (result.getSuccess()) {
            String username = extractUsernameFromLogin(result);
            if (username != null) {
                currentUsername = username;
                ApiResult userResult = userClient.getUserByUsername(username);
                UserView.displayUserResult(userResult);
            }
        }
    }

    private static void handleRegister(Scanner scanner) {
        System.out.println("==== REGISTER ====");
        System.out.print("Username: ");
        String username = scanner.nextLine();

        System.out.print("Email: ");
        String email = scanner.nextLine();

        System.out.print("Password: ");
        String password = scanner.nextLine();

        System.out.print("Phone Number: ");
        String phoneNumber = scanner.nextLine();

        System.out.print("Description: ");
        String description = scanner.nextLine();

        UserRegisterRequestDto registerRequest = new UserRegisterRequestDto(username, email, password, phoneNumber, description);
        ApiResult result = userClient.register(registerRequest);

        System.out.println(result.getMessage());
        if (result.getSuccess()) {
            currentUsername = username;
            ApiResult userResult = userClient.getUserByUsername(username);
            UserView.displayUserResult(userResult);
        }
    }

    private static String extractUsernameFromLogin(ApiResult loginResult) {
        try {
            ObjectMapper mapper = new ObjectMapper();
            mapper.findAndRegisterModules(); // pentru  datetime
            UserResponseDto user = mapper.readValue(loginResult.getResponseBody(), UserResponseDto.class);
            return user.username(); // getter pt username
        } catch (Exception e) {
            System.out.println("Couldn't parse login response to extract username.");
            return null;
        }
    }
}
