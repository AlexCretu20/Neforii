package ui;

import model.User;
import service.IUserService;
import validation.UserValidator;

import java.util.Scanner;

public class UserUI {
    private final Scanner scanner;
    private final IUserService userService;
    private final UserValidator userValidator;

    public UserUI(Scanner scanner, IUserService userService, UserValidator userValidator) {
        this.scanner = scanner;
        this.userService = userService;
        this.userValidator = userValidator;
    }

    public void registerUserUI() {
        System.out.println("--- Register ---");

        String username;
        while (true) {
            System.out.print("Username: ");
            username = scanner.nextLine().trim();

            if (userService.isUsernameExisting(username)) {
                System.out.println("Username already exists!");
            } else break;
        }

        String email;
        while (true) {
            System.out.print("Email: ");
            email = scanner.nextLine().trim();

            if (!userValidator.isEmailValid(email)) {
                System.out.println("The format is not valid! Exemple: something@gmail.com ");
            } else if (userService.isEmailExisting(email)) {
                System.out.println("Email already used");
            } else break;
        }

        String password;
        while (true) {
            System.out.print("Password: ");
            password = scanner.nextLine();

            if (!userValidator.isPasswordValid(password)) {
                System.out.println("The password must have at least 8 characters");
            } else break;
        }

        String phoneNumber;
        while (true) {
            System.out.print("Phone Number: ");
            phoneNumber = scanner.nextLine().trim();

            if (!userValidator.isPhoneNumberValid(phoneNumber)) {
                System.out.println("Invalid phone number! Valid format: 07xx xxx xxx");
            } else break;
        }

        System.out.print("Description: ");
        String description = scanner.nextLine().trim();

        User user = new User(username, email, password, phoneNumber, description);
        userService.registerUser(user);
        System.out.println("User " + username + " registered successfully!");
    }


    public void loginUserUI() {
        System.out.println("--- Login ---");
        System.out.print("Email: ");
        String email = scanner.nextLine().trim();

        System.out.print("Password: ");
        String password = scanner.nextLine().trim();

        User user = userService.loginUser(email, password);
        if (user != null) {
            userService.setCurrentUser(user);
            System.out.println("Welcome " + user.getUsername() + "!");
        } else {
            System.out.println("Invalid email or password! Please try again!");
        }

    }

    public void logoutUserUI() {
        String username = userService.getCurrentUser().getUsername();
        userService.logoutUser();
        System.out.println("\n"+username + " has logged out! See you next time!");
    }
}
