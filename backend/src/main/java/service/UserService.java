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

        String username;
        while(true) {
            System.out.print("Username: ");
            username = sc.nextLine().trim();

            boolean userExists = false;
            for (User u : users) {
                if (u.getUsername().equals(username)) {
                    System.out.println("Username is already in use!");
                    userExists = true;
                }
            }
            if(!userExists){
                break;
            }
        }

        String email;
        while(true) {
            System.out.print("Email: ");
            email = sc.nextLine().trim();

            boolean isNotOk=false;
            if (!email.matches("^\\S+@\\S+\\.\\S+$")) {
                System.out.println("Invalid email format.");
                isNotOk=true;
            }

            for (User u : users) {
                if (u.getEmail().equals(email)) {
                    System.out.println("Email already used.");
                    isNotOk=true;
                }
            }
            if(!isNotOk){
                break;
            }
        }

        String password;
        while(true) {
            System.out.print("Password: ");
            password = sc.nextLine();

            boolean isNotOk=false;
            if (password.length() < 8) {
                System.out.println("Password must have at least 8 characters.");
                isNotOk=true;
            }

            if(!isNotOk){
                break;
            }
        }

        String phoneNumber;
        while(true) {
            System.out.print("Phone Number: ");
            phoneNumber = sc.nextLine().trim();

            boolean isNotOk=false;
            if (!phoneNumber.matches("^07[0-9]{8}$")) {
                System.out.println("Invalid phone number.");
                isNotOk=true;
            }
            if(!isNotOk){
                break;
            }
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

    public void logoutUser(){
        currentUser=null;
        System.out.println("You have been logged out successfully! See you next time!");
    }

    public User getCurrentUser() {
        return currentUser;
    }

    public void showUsers(){
        System.out.println("--- Users list ---");
        for(User u:users){
            System.out.println(u);
        }
    }
}


