package ro.neforii.service;

import org.springframework.stereotype.Service;
import ro.neforii.dto.user.update.UserUpdateRequestDto;
import ro.neforii.exception.EmailAlreadyInUseException;
import ro.neforii.exception.UserNotFoundException;
import ro.neforii.exception.UsernameAlreadyInUseException;
import ro.neforii.model.User;
import ro.neforii.repository.UserRepository;
import ro.neforii.service.crud.CrudService;

import java.util.List;
import java.util.Optional;

@Service
public class UserService implements CrudService<User, Integer, UserUpdateRequestDto> {

    private User currentUser;
    private final UserRepository userRepo;

    public UserService(UserRepository userRepo) {
        this.userRepo = userRepo;
    }

    //CREATE methods
    @Override
    public User create(User user) {
        return userRepo.save(user);
    }

    //READ methods
    @Override
    public Optional<User> findById(Integer id) {
        return userRepo.findById(id);
    }

    @Override
    public List<User> findAll() {
        return userRepo.findAll();
    }

    //UPDATE methods
    @Override
    public User update(Integer id, UserUpdateRequestDto requestDto) {
        Optional<User> existingUserOpt = findById(id);
        if (existingUserOpt.isEmpty()) {
            throw new UserNotFoundException("The user with id " + id + " does not exist.");
        }
        User existingUser = existingUserOpt.get();
        if (!(requestDto.username().equals(existingUser.getUsername())) && isUsernameExisting(requestDto.username())) {
            throw new UsernameAlreadyInUseException("The username " + requestDto.username() + " is already in use.");
        }
        if (!(requestDto.email().equals(existingUser.getEmail())) && isEmailExisting(requestDto.email())) {
            throw new EmailAlreadyInUseException("The email " + requestDto.email() + " is already in use.");
        }

        existingUser.setUsername(requestDto.username());
        existingUser.setEmail(requestDto.email());
        existingUser.setPassword(requestDto.password());
        existingUser.setPhoneNumber(requestDto.phoneNumber());
        existingUser.setDescription(requestDto.description());

        return userRepo.save(existingUser);
    }

    //DELETE methods
    @Override
    public void deleteById(Integer id) {
        userRepo.deleteById(id);
        //stergem current User
        if (currentUser != null && currentUser.getId() == id) {
            currentUser = null;
        }
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

    public User loginUser(String email, String password) {
        Optional<User> user = userRepo.findByEmailAndPassword(email, password);
        if (user.isPresent()) {
            return user.get();
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

    public void deleteUser(User user) {
        userRepo.deleteById(user.getId());
        logoutUser();
    }
}


