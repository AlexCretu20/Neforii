package ro.neforii.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ro.neforii.dto.user.UserResponseDto;
import ro.neforii.dto.user.login.UserLoginRequestDto;
import ro.neforii.model.User;
import ro.neforii.service.IUserService;

@RestController
@RequestMapping("/user")
public class UserController {
    private final IUserService userService;

    public UserController(IUserService userService) {
        this.userService = userService;
    }

    @PostMapping("/login")
    public ResponseEntity<UserResponseDto> login(@RequestBody UserLoginRequestDto request) {
        User user = userService.loginUser(request.email(), request.password());
        if (user == null) {
            return ResponseEntity
                    .status(HttpStatus.UNAUTHORIZED) // 401 daca nu se logheaza bn
                    .build();//null body
        }

        //user exists
        UserResponseDto userResponseDto = new UserResponseDto(
                user.getId(),
                user.getUsername(),
                user.getEmail(),
                user.getPhoneNumber(),
                user.getDescription()
        );

        return ResponseEntity
                .ok(userResponseDto);
    }
}
