package models.user;

public record UserUpdateRequestDto(
        String username,
        String email,
        String password,
        String phoneNumber,
        String description
) {
}
