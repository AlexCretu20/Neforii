package ro.neforii.dto.user.update;

public record UserUpdateRequestDto(
        String username,
        String email,
        String password,
        String phoneNumber,
        String description
) {
}
