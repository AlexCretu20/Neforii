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

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class UserService implements CrudService<UserResponseDto, UUID, UserRegisterRequestDto, UserUpdateRequestDto> {

    private final UserMapper userMapper;
    private User currentUser;
    private final UserRepository userRepo;

    public UserService(UserRepository userRepo, UserMapper userMapper) {
        this.userRepo = userRepo;
        this.userMapper = userMapper;
    }

    //CREATE methods
    @Override
    public UserResponseDto create(UserRegisterRequestDto dto) {
        if (userRepo.existsByUsername(dto.username())) {
            throw new UsernameAlreadyInUseException(dto.username());
        }
        if (userRepo.findByEmail(dto.email()).isPresent()) {
            throw new EmailAlreadyInUseException(dto.email());
        }
        User user = userMapper.userRegisterRequestDtoToUser(dto);
        return userMapper.userToUserResponseDto(userRepo.save(user));
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

    //metoda pentru comunicare cu controller, pentru mai putina logica in controller, returneaza responseDto
    public UserResponseDto findByUsername(String username) {
        Optional<User> user = userRepo.findByUsername(username);
        if(user.isEmpty()){
            throw new UserNotFoundException("Couldn't find user with username: " + username);
        }
        return userMapper.userToUserResponseDto(user.get());
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
        Optional<User> user = userRepo.findByEmailAndPassword(userLoginRequestDto.email(), userLoginRequestDto.password());
        if(user.isEmpty()){
            throw new InvalidUserLoginException("Invalid email or password.");
        }
        return userMapper.userToUserResponseDto(user.get());
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

    public User getUserByUsername(String username) {
        return userRepo.findByUsername(username).orElseThrow(() -> new UserNotFoundException("User with username " + username + " not found."));
    }

    public User getUserByEmail(String email) {
        return userRepo.findByEmail(email).orElseThrow(() -> new UserNotFoundException("User with email " + email + " not found."));
    }
}


