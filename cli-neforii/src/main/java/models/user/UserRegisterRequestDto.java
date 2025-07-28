package models.user;

public record UserRegisterRequestDto(
        String username,
        String email,
        String password,
        String phoneNumber,
        String description
) {
}
