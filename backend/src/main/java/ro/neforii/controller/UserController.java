package ro.neforii.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ro.neforii.dto.user.UserResponseDto;
import ro.neforii.dto.user.login.UserLoginRequestDto;
import ro.neforii.dto.user.register.UserRegisterRequestDto;
import ro.neforii.dto.user.update.UserUpdateRequestDto;
import ro.neforii.service.UserService;

import java.util.UUID;

@RestController
@RequestMapping("/users")
public class UserController {
    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping("/login")
    public ResponseEntity<UserResponseDto> login(@RequestBody UserLoginRequestDto request) {
        UserResponseDto user = userService.loginUser(request); //UNAUTHROIZED daca nu e ok log info
        System.out.println("a trecut prin logare");
        return ResponseEntity
                .ok(user);
    }

    @PostMapping("/register")
    public ResponseEntity<UserResponseDto> register(@RequestBody UserRegisterRequestDto request) {
        UserResponseDto created = userService.create(request);
        return ResponseEntity
                .status(HttpStatus.CREATED) // 201 ca s-a creat
                .body(created);
    }

    @GetMapping("/{username}")
    public ResponseEntity<UserResponseDto> getUserByUsername(@PathVariable String username) {
        UserResponseDto user = userService.findByUsername(username);//NOT_FOUND daca nu exista userul
        return ResponseEntity
                .ok(user);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteUser(@PathVariable UUID id) {
        userService.deleteById(id);
        return ResponseEntity
                .noContent()
                .build(); // 204 adica e succes si No Content(ca a fost sters)
    }

    @PutMapping("/{id}")
    public ResponseEntity<UserResponseDto> updateUser(@PathVariable UUID id, @RequestBody UserUpdateRequestDto request) {
        UserResponseDto updatedUser = userService.update(id, request);
        return ResponseEntity
                .ok(updatedUser);
    }
}
