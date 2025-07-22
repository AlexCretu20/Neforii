package ro.neforii.dto.user;

public record UserResponseDto(
        int id,
        String username,
        String email,
        String phoneNumber,
        String description
) {}
