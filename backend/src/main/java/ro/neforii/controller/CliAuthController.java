package ro.neforii.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ro.neforii.model.User;
import ro.neforii.service.FakeUserAuthService;
import ro.neforii.service.UserService;

@RestController
@RequestMapping("/cli/auth")
@RequiredArgsConstructor
public class CliAuthController { // in cli apelez asta ca sa nu mai fie hardcodat userul in FakeUserAuthService
    private final UserService userService;
    private final FakeUserAuthService fakeUserAuthService;

    @PostMapping("/set-user-by-email/{email}")
    public ResponseEntity<String> setClientUserId(@PathVariable String email) { // refactorizez dupa
        User user = userService.getUserByEmail(email);
        fakeUserAuthService.setClientUserId(user.getId());
        return ResponseEntity.ok("User set successfully");
    }
}
