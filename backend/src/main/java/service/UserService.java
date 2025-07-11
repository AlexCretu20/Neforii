package service;

import model.User;

import java.util.ArrayList;
import java.util.List;

public class UserService implements IUserService {
    private User currentUser;
    private final List<User> users = new ArrayList<>();
    private static UserService instance;
    private static int userId = 0;

    private UserService() {
    }

    public static UserService getInstance() {
        if (instance == null) {
            instance = new UserService();
        }
        return instance;
    }

    public void registerUser(User user) {
        user.setId(++userId);
        users.add(user);
    }

    public boolean isUsernameExisting(String username) {
        for (User user : users) {
            if (user.getUsername().equals(username)) {
                return true;
            }
        }
        return false;
    }

    public boolean isEmailExisting(String email) {
        for (User user : users) {
            if (user.getEmail().equals(email)) {
                return true;
            }
        }
        return false;
    }


    public User loginUser(String email, String password) {
        for (User u : users) {
            if (u.getEmail().equals(email) && u.getPassword().equals(password)) {
                return u;
            }
        }
        return null;
    }

    public void logoutUser() {
        currentUser = null;
    }

    public User getCurrentUser() {
        return currentUser;
    }

    public void setCurrentUser(User currentUser) {
        this.currentUser = currentUser;
    }
}


