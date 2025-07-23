package ro.neforii.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ro.neforii.dto.user.register.UserRegisterRequestDto;
import ro.neforii.dto.user.update.UserUpdateRequestDto;
import ro.neforii.mapper.UserMapper;
import ro.neforii.dto.user.UserResponseDto;
import ro.neforii.dto.user.login.UserLoginRequestDto;
import ro.neforii.model.User;
import ro.neforii.service.IUserService;

@RestController
@RequestMapping("/user")
public class UserController {
    private final IUserService userService;
    private final UserMapper userMapper;

    public UserController(IUserService userService, UserMapper userMapper) {
        this.userService = userService;
        this.userMapper = userMapper;
    }

    @PostMapping("/login")
    public ResponseEntity<UserResponseDto> login(@RequestBody UserLoginRequestDto request) {
        User user = userService.loginUser(request.email(), request.password());
        if (user == null) {
            return ResponseEntity
                    .status(HttpStatus.UNAUTHORIZED) // 401 daca nu se logheaza bn
                    .build(); //null body
        }
        return ResponseEntity.ok(userMapper.userToUserResponseDto(user));
    }

    @PostMapping("/register")
    public ResponseEntity<UserResponseDto> register(@RequestBody UserRegisterRequestDto request) {
        if (userService.isUsernameExisting(request.username())) {
            return ResponseEntity
                    .status(HttpStatus.BAD_REQUEST) // 400 daca username-ul e deja folosit
                    .build(); //null body
        }
        if (userService.isEmailExisting(request.email())) {
            return ResponseEntity
                    .status(HttpStatus.BAD_REQUEST) // 400 daca email-ul e deja folosit
                    .build(); //null body
        }

        User user = userMapper.userRegisterRequestDtoToUser(request);
        userService.registerUser(user);
        return ResponseEntity
                .status(HttpStatus.CREATED) // 201 ca s-a creat
                .body(userMapper.userToUserResponseDto(user));
    }

    @GetMapping("/{username}")
    public ResponseEntity<UserResponseDto> getUserByUsername(@PathVariable String username) {
        User user = userService.findByUsername(username);
        if (user == null) {
            return ResponseEntity
                    .status(HttpStatus.NOT_FOUND) // 404 daca nu gaseste userul
                    .build();
        }
        return ResponseEntity
                .ok(userMapper.userToUserResponseDto(user));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteUser(@PathVariable int id) {
        User user = userService.findById(id);
        if (user == null) {
            return ResponseEntity
                    .status(HttpStatus.NOT_FOUND) // 404 daca nu gaseste userul
                    .build();
        }
        userService.deleteUser(user);
        return ResponseEntity
                .noContent()
                .build(); // 204 adica e succes si No Content(ca a fost sters)
    }

    @PutMapping("/{id}")
    public ResponseEntity<UserResponseDto> updateUser(@PathVariable int id, @RequestBody UserUpdateRequestDto request) {
        User mappedUser = userMapper.userUpdateRequestDtoToUser(request);
        User updatedUser = userService.updateUser(id, mappedUser);
        if (updatedUser == null) {
            return ResponseEntity
                    .status(HttpStatus.NOT_FOUND) // 404 daca nu gaseste userul
                    .build();
        }
        return ResponseEntity
                .ok(userMapper.userToUserResponseDto(updatedUser));
    }
}
