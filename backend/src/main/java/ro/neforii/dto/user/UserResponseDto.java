package ro.neforii.dto.user;

import java.time.LocalDateTime;

public record UserResponseDto(
        int id,
        String username,
        String email,
        String phoneNumber,
        String description,
        LocalDateTime createdAt
) {
}
