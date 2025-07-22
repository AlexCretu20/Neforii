package ro.neforii.service;

import org.springframework.stereotype.Service;
import ro.neforii.model.User;
import ro.neforii.repository.UserRepository;

import java.util.Optional;

@Service
public class UserService implements IUserService {
    private User currentUser;

    private final UserRepository userRepo;

    public UserService(UserRepository userRepo){
        this.userRepo = userRepo;
    }

    public void registerUser(User user) {
        userRepo.save(user);
    }

    public boolean isUsernameExisting(String username) {
        return userRepo.isUsernameTaken(username);
    }

    public boolean isEmailExisting(String email) {
        return userRepo.findByEmail(email).isPresent();
    }


    public User loginUser(String email, String password) {
        Optional<User> user = userRepo.findByEmailAndPassword(email, password);
        if (user.isPresent()) {
            this.currentUser = user.get();
            return this.currentUser;
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

    public void deleteUser(User user){
        userRepo.deleteById(user.getId());
        logoutUser();
    }
}


