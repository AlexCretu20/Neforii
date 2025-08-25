package handlers;
import client.UserClient;
import config.ConnectionConfig;
import models.ApiResult;
import models.user.UserLoginRequestDto;
import models.user.UserRegisterRequestDto;
import models.user.UserResponseDto;
import views.UserView;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.Scanner;
import java.util.UUID;

public class UserHandler {
    private static final UserClient userClient = new UserClient(ConnectionConfig.BASE_URL + "/users");

    public static String currentUsername = null;
    public static UUID currentUserId = null;

    public static void handleLogin(Scanner scanner, Runnable postLoginMenu) {
        System.out.println("==== LOGIN ====");
        System.out.print("Email: ");
        String email = scanner.nextLine();

        System.out.print("Password: ");
        String password = scanner.nextLine();

        UserLoginRequestDto loginRequest = new UserLoginRequestDto(email, password);
        ApiResult result = userClient.login(loginRequest);

        UserView.displayUserResult(result);
        if (result.getSuccess()) {
            currentUsername = extractUsernameFromLogin(result);
            currentUserId = extractIdFromLogin(result);

            postLoginMenu.run();
        }
    }

    public static void handleRegister(Scanner scanner, Runnable postLoginMenu) {
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
            currentUserId = userClient.extractUserId(userResult);
            postLoginMenu.run();
        }
    }

    private static String extractUsernameFromLogin(ApiResult loginResult) {
        try {
            ObjectMapper mapper = new ObjectMapper();
            mapper.findAndRegisterModules();
            UserResponseDto user = mapper.readValue(loginResult.getResponseBody(), UserResponseDto.class);
            return user.username();
        } catch (Exception e) {
            System.out.println("Couldn't parse login response to extract username.");
            return null;
        }
    }

    private static UUID extractIdFromLogin(ApiResult loginResult) {
        try {
            ObjectMapper mapper = new ObjectMapper();
            mapper.findAndRegisterModules();
            UserResponseDto user = mapper.readValue(loginResult.getResponseBody(), UserResponseDto.class);
            return user.id();
        } catch (Exception e) {
            System.out.println("Couldn't parse login response to extract id.");
            return null;
        }
    }
}
