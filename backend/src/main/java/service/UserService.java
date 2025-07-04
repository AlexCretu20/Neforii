package service;

import model.User;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class UserService {
    private User currentUser;
    private List<User> users = new ArrayList<>();
    private Scanner sc = new Scanner(System.in);
    private static UserService instance;
    private static int userId=0;

    private UserService(){}

    public static UserService getInstance(){
        if(instance==null){
            instance=new UserService();
        }
        return instance;
    }

    public void registerUser() {

        System.out.println("--- Register ---");

        System.out.print("Name: ");
        String username = sc.nextLine().trim();

        for (User u : users) {
            if (u.getUsername().equals(username)) {
                System.out.println("Username is already in use!");
                return;
            }
        }

        System.out.print("Email: ");
        String email = sc.nextLine().trim();
        if (!email.matches("^\\S+@\\S+\\.\\S+$")) {
            System.out.println("Invalid email format.");
            return;
        }

        for (User u : users) {
            if (u.getEmail().equals(email)) {
                System.out.println("Email already used.");
                return;
            }
        }

        System.out.print("Password: ");
        String password = sc.nextLine();
        if (password.length() < 6) {
            System.out.println("Password too short.");
            return;
        }

        System.out.print("Phone Number: ");
        String phoneNumber = sc.nextLine().trim();
        if (!phoneNumber.matches("^07[0-9]{8}$")) {
            System.out.println("Invalid phone number.");
            return;
        }

        System.out.print("Description: ");
        String description = sc.nextLine().trim();

        User user = new User(username, email, password, phoneNumber, description);
        user.setId(++userId);
        users.add(user);
        System.out.println("User " + username + " registered successfully!");
    }

    public User loginUser() {
        System.out.println("--- Login ---");
        System.out.print("Email: ");
        String email = sc.nextLine().trim();

        System.out.print("Password: ");
        String password = sc.nextLine().trim();

        for (User u : users) {
            if (u.getEmail().equals(email)) {
                if (u.getPassword().equals(password)) {
                    currentUser = u;
                    System.out.println("Welcome, " + u.getUsername() + "!");
                    return currentUser;
                } else {
                    System.out.println("Incorrect password! Please try again!");
                    return null;
                }
            }
        }

        System.out.println("No user found with that email.");
        return null;
    }

    public void showUsers(){
        System.out.println("--- Users list ---");
        for(User u:users){
            System.out.println(u);
        }
    }
}


