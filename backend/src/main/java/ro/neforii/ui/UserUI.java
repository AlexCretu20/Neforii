package ro.neforii.ui;

import ro.neforii.model.User;
import ro.neforii.service.IUserService;
import ro.neforii.validation.UserValidator;

import java.sql.SQLOutput;
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

    public void displayUserProfile(User user){
        System.out.println("\n--- My Details ---");
        System.out.println("Username: " + user.getUsername());
        System.out.println("Email: " + user.getEmail());
        System.out.println("Phone Number: " + user.getPhoneNumber());
        System.out.println("Description: " + user.getDescription());
        System.out.println("Created at: " + user.getCreatedAt());
    }

    public void deleteUser(User user) {
        String response;

        while (true) {
            System.out.print("Are you sure that you want to delete your account? (y/n): ");
            response = scanner.nextLine().trim().toLowerCase();

            if (response.equals("y")) {
                userService.deleteUser(user);
                System.out.println("Account deleted successfully!");
                break;
            } else if (response.equals("n")) {
                System.out.println("Account deletion canceled.");
                break;
            } else {
                System.out.println("Invalid input. Please enter 'y' or 'n'.");
            }
        }
    }

}
