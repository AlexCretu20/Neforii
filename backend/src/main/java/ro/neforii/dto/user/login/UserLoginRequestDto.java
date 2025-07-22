package ro.neforii.dto.user.login;

public record UserLoginRequestDto(
        String email,
        String password
) {}