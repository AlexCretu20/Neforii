package models.user;

public record UserLoginRequestDto(
        String email,
        String password
) {
}