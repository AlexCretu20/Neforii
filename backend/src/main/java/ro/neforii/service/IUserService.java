package ro.neforii.service;

import ro.neforii.model.User;

import java.util.UUID;

public interface IUserService {
    void registerUser(User user);

    boolean isUsernameExisting(String username);

    boolean isEmailExisting(String email);

    User loginUser(String email, String password);

    void logoutUser();

    User getCurrentUser();

    void setCurrentUser(User user);

    void deleteUser(User user);

    User findByUsername(String username);

    User findById(UUID id);

    User updateUser(UUID id, User user);
}
