package models.user;

import java.time.LocalDateTime;
import java.util.UUID;

public record UserResponseDto(
        UUID id,
        String username,
        String email,
        String phoneNumber,
        String description,
        LocalDateTime createdAt
) {
}
