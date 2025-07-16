package service;

import model.User;
import repository.UserRepository;

import java.util.Optional;

public class UserService implements IUserService {
    private User currentUser;
    //private static UserService instance;
    private final UserRepository userRepo;

//    private UserService() {
//    }
//
//    public static UserService getInstance() {
//        if (instance == null) {
//            instance = new UserService();
//        }
//        return instance;
//    }

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
}


