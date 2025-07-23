package ro.neforii.dto.user.register;

public record UserRegisterRequestDto(
        String username,
        String email,
        String password,
        String phoneNumber,
        String description
) {
}
