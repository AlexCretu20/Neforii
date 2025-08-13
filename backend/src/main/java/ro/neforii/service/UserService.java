package ro.neforii.service;

import org.springframework.stereotype.Service;
import ro.neforii.dto.user.UserResponseDto;
import ro.neforii.dto.user.login.UserLoginRequestDto;
import ro.neforii.dto.user.register.UserRegisterRequestDto;
import ro.neforii.dto.user.update.UserUpdateRequestDto;
import ro.neforii.exception.user.EmailAlreadyInUseException;
import ro.neforii.exception.user.InvalidUserLoginException;
import ro.neforii.exception.user.UserNotFoundException;
import ro.neforii.exception.user.UsernameAlreadyInUseException;
import ro.neforii.mapper.UserMapper;
import ro.neforii.model.User;
import ro.neforii.repository.UserRepository;
import ro.neforii.service.crud.CrudService;
import ro.neforii.utils.logger.Logger;
import ro.neforii.utils.logger.LoggerType;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class UserService implements CrudService<UserResponseDto, UUID, UserRegisterRequestDto, UserUpdateRequestDto> {

    private final UserMapper userMapper;
    private User currentUser;
    private final UserRepository userRepo;
    private final FakeUserAuthService fakeUserAuthService;
    private static final String LOG_PREFIX = "UserService: ";
    public UserService(UserRepository userRepo, UserMapper userMapper, FakeUserAuthService fakeUserAuthService) {
        this.userRepo = userRepo;
        this.userMapper = userMapper;
        this.fakeUserAuthService = fakeUserAuthService;
    }

    //CREATE methods
    @Override
    public UserResponseDto create(UserRegisterRequestDto dto) {
        Logger.log(LoggerType.DEBUG, LOG_PREFIX + "Attempting to register user with username: " + dto.username());
        try {
            if (userRepo.existsByUsername(dto.username())) {
                Logger.log(LoggerType.WARNING, LOG_PREFIX + "Username already in use: " + dto.username());
                throw new UsernameAlreadyInUseException(dto.username());
            }
            if (userRepo.findByEmail(dto.email()).isPresent()) {
                Logger.log(LoggerType.WARNING, LOG_PREFIX + "Email already in use: " + dto.email());
                throw new EmailAlreadyInUseException(dto.email());
            }
            User user = userMapper.userRegisterRequestDtoToUser(dto);
            UserResponseDto userResponseDto = userMapper.userToUserResponseDto(userRepo.save(user));

            fakeUserAuthService.setClientUserId(user.getId());
            Logger.log(LoggerType.INFO, LOG_PREFIX + "User registered successfully: " + user.getUsername());

            return userResponseDto;
        } catch (Exception e) {
            Logger.log(LoggerType.ERROR, LOG_PREFIX + "Failed to register user: " + e.getMessage());
            throw e;
        }
    }

    //READ methods
    @Override
    public UserResponseDto findById(UUID id) {
        Optional<User> userOpt = userRepo.findById(id);
        if (userOpt.isEmpty()) {
            throw new UserNotFoundException("User with id " + id + " not found.");
        }
        return userMapper.userToUserResponseDto(userOpt.get());
    }

    @Override
    public List<UserResponseDto> findAll() {
        return userRepo.findAll()
                .stream()
                .map(userMapper::userToUserResponseDto)
                .toList();
    }

    //UPDATE methods
    @Override
    public UserResponseDto update(UUID id, UserUpdateRequestDto requestDto) {
        Optional<User> existingUserOpt = userRepo.findById(id);
        if (existingUserOpt.isEmpty()) {
            throw new UserNotFoundException("The user with id " + id + " does not exist.");
        }
        User existingUser = existingUserOpt.get();
        if (!requestDto.username().equals(existingUser.getUsername()) && isUsernameExisting(requestDto.username())) {
            throw new UsernameAlreadyInUseException(requestDto.username());
        }
        if (!requestDto.email().equals(existingUser.getEmail()) && isEmailExisting(requestDto.email())) {
            throw new EmailAlreadyInUseException(requestDto.email());
        }

        existingUser.setUsername(requestDto.username());
        existingUser.setEmail(requestDto.email());
        existingUser.setPassword(requestDto.password());
        existingUser.setPhoneNumber(requestDto.phoneNumber());
        existingUser.setDescription(requestDto.description());

        return userMapper.userToUserResponseDto(userRepo.save(existingUser));
    }

    //DELETE methods
    @Override
    public void deleteById(UUID id) {
        Logger.log(LoggerType.DEBUG, LOG_PREFIX + "Deleting user with ID: " + id);
        try {
            userRepo.deleteById(id);
            //stergem current User
            if (currentUser != null && currentUser.getId().equals(id)) {
                currentUser = null;
            }
            Logger.log(LoggerType.INFO, LOG_PREFIX + "User with ID " + id + " deleted successfully");
        } catch (Exception e) {
            Logger.log(LoggerType.ERROR, LOG_PREFIX + "Failed to delete user with ID " + id + ": " + e.getMessage());
            throw e;
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

    //metoda pentru comunicare cu controller, pentru mai putina logica in controller, returneaza responseDto
    public UserResponseDto findByUsername(String username) {
        Logger.log(LoggerType.DEBUG, LOG_PREFIX + "Looking up user by username: " + username);
        try {
            Optional<User> user = userRepo.findByUsername(username);
            if (user.isEmpty()) {
                Logger.log(LoggerType.WARNING, LOG_PREFIX + "User not found with username: " + username);
                throw new UserNotFoundException("Couldn't find user with username: " + username);
            }
            Logger.log(LoggerType.DEBUG, LOG_PREFIX + "Successfully retrieved user: " + username);
            return userMapper.userToUserResponseDto(user.get());
        } catch (Exception e) {
            Logger.log(LoggerType.ERROR, LOG_PREFIX + "Error retrieving user by username: " + e.getMessage());
            throw e;
        }
    }
    //METODA pentru a gasi un User dupa username, returneaza User entity, metoda e folosita intern de alte servicii, nu controllere
    public User findUserEntityByUsername(String username) {
        Optional<User> user = userRepo.findByUsername(username);
        if(user.isEmpty()){
            throw new UserNotFoundException("Couldn't find user with username: " + username);
        }
        return user.get();
    }

    public UserResponseDto loginUser(UserLoginRequestDto userLoginRequestDto) {
        Logger.log(LoggerType.DEBUG, LOG_PREFIX + "Attempting login for email: " + userLoginRequestDto.email());
        try {
            Optional<User> user = userRepo.findByEmailAndPassword(userLoginRequestDto.email(), userLoginRequestDto.password());
            if(user.isEmpty()){
                Logger.log(LoggerType.WARNING, LOG_PREFIX + "Invalid login attempt for email: " + userLoginRequestDto.email());
                throw new InvalidUserLoginException("Invalid email or password.");
            }
            fakeUserAuthService.setClientUserId(user.get().getId());
            Logger.log(LoggerType.INFO, LOG_PREFIX + "User logged in successfully: " + user.get().getUsername());
            return userMapper.userToUserResponseDto(user.get());
        } catch (Exception e) {
            Logger.log(LoggerType.ERROR, LOG_PREFIX + "Login failed: " + e.getMessage());
            throw e;
        }
    }

    public void logoutUser() {
        fakeUserAuthService.setClientUserId(null);
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

    public User getUserByUsername(String username) {
        Logger.log(LoggerType.DEBUG, LOG_PREFIX + "Looking up user by username: " + username);
        try {
            User user = userRepo.findByUsername(username)
                    .orElseThrow(() -> {
                        Logger.log(LoggerType.WARNING, LOG_PREFIX + "User not found with username: " + username);
                        return new UserNotFoundException("User with username " + username + " not found.");
                    });
            Logger.log(LoggerType.DEBUG, LOG_PREFIX + "Successfully retrieved user: " + username);
            return user;
        } catch (Exception e) {
            Logger.log(LoggerType.ERROR, LOG_PREFIX + "Error retrieving user by username: " + e.getMessage());
            throw e;
        }
    }

    public User getUserByEmail(String email) {
        return userRepo.findByEmail(email).orElseThrow(() -> new UserNotFoundException("User with email " + email + " not found."));
    }
}


