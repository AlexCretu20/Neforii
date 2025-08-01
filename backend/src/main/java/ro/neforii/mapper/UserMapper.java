package ro.neforii.mapper;

import org.springframework.stereotype.Component;
import ro.neforii.dto.user.UserResponseDto;
import ro.neforii.dto.user.register.UserRegisterRequestDto;
import ro.neforii.dto.user.update.UserUpdateRequestDto;
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
                user.getDescription(),
                user.getCreatedAt()
        );
    }

    public User userRegisterRequestDtoToUser(UserRegisterRequestDto userRegisterRequestDto) {
        if (userRegisterRequestDto == null) {
            return null;
        }
        return new User(
                userRegisterRequestDto.username(),
                userRegisterRequestDto.email(),
                userRegisterRequestDto.password(),
                userRegisterRequestDto.phoneNumber(),
                userRegisterRequestDto.description()
        );
    }

    public User userUpdateRequestDtoToUser(UserUpdateRequestDto userUpdateRequestDto) {
        if (userUpdateRequestDto == null) {
            return null;
        }
        return new User(
                userUpdateRequestDto.username(),
                userUpdateRequestDto.email(),
                userUpdateRequestDto.password(),
                userUpdateRequestDto.phoneNumber(),
                userUpdateRequestDto.description()
        );
    }

    public User UserResponseToUser(UserResponseDto userDto){
        if (userDto == null){
            return null;
        }
        User user = new User();
        user.setUsername(userDto.username());
        user.setEmail(userDto.email());
        user.setPhoneNumber(userDto.phoneNumber());
        user.setDescription(userDto.description());
        user.setCreatedAt(userDto.createdAt());
        return  user;
    }
}
