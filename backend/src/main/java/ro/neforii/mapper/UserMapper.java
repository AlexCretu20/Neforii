package ro.neforii.mapper;

import org.springframework.stereotype.Component;
import ro.neforii.dto.user.UserResponseDto;
import ro.neforii.model.User;

@Component
public class UserMapper {
    public UserResponseDto userToUserResponseDto(User user) {
        if (user == null) {
            return null;
        }
        return new UserResponseDto(
                user.getId(),
                user.getUsername(),
                user.getEmail(),
                user.getPhoneNumber(),
                user.getDescription()
        );
    }
}
