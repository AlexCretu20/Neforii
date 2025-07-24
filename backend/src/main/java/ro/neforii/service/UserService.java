package ro.neforii.service;

import org.springframework.stereotype.Service;
import ro.neforii.model.User;
import ro.neforii.repository.UserRepository;

import java.util.Optional;

@Service
public class UserService implements IUserService {
    private User currentUser;

    private final UserRepository userRepo;

    public UserService(UserRepository userRepo) {
        this.userRepo = userRepo;
    }

    public void registerUser(User user) {
        userRepo.save(user);
    }

    public boolean isUsernameExisting(String username) {
        return userRepo.existsByUsername(username);
    }

    public boolean isEmailExisting(String email) {
        return userRepo.findByEmail(email).isPresent();
    }

    public User findByUsername(String username) {
        Optional<User> user = userRepo.findByUsername(username);
        if (user.isPresent()) {
            return user.get();
        }
        return null;
    }

    public User findById(int id) {
        Optional<User> user = userRepo.findById(id);
        if (user.isPresent()) {
            return user.get();
        }
        return null;
    }

    public User loginUser(String email, String password) {
        Optional<User> user = userRepo.findByEmailAndPassword(email, password);
        if (user.isPresent()) {
            return user.get();
        }
        return null;
    }

    public User updateUser(int id, User user) {
        Optional<User> existingUserOpt = userRepo.findById(id);
        if (existingUserOpt.isEmpty()) {
            return null;
        }
        //verific daca modelul update e acelasi, ca sa nu ocup DB pool degeaba
        User existingUser = existingUserOpt.get();
        //setez campurile care nu se pot schimba pt equals
        user.setId(id);
        user.setCreatedAt(existingUser.getCreatedAt());
        if (existingUser.equals(user)) {
            return existingUser;
        }

        return userRepo.save(user);  //daca Spring-ul vede ca id-ul e deja folosit face update in loc de insert
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

    public void deleteUser(User user) {
        userRepo.deleteById(user.getId());
        logoutUser();
    }
}


