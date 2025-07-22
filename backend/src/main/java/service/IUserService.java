package service;

import model.User;

public interface IUserService {
    void registerUser(User user);
    boolean isUsernameExisting(String username);
    boolean isEmailExisting(String email);
    User loginUser(String email, String password);
    void logoutUser();
    User getCurrentUser();
    void setCurrentUser(User user);
    void deleteUser(User user);
}
