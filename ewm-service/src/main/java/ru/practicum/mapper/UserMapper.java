package ru.practicum.mapper;

import ru.practicum.exception.ValidationException;
import ru.practicum.model.User;
import ru.practicum.dto.user.NewUserRequest;
import ru.practicum.dto.user.UserDto;
import ru.practicum.dto.user.UserShortDto;

public class UserMapper {
    public static UserShortDto toUserShortDtoFromUser(User user) {
        if (user == null) {
            throw new ValidationException("user can not be null");
        }
        UserShortDto dto = new UserShortDto();
        dto.setId(user.getId());
        dto.setName(user.getName());
        return dto;
    }

    public static UserDto toUserDtoFromUser(User user) {
        if (user == null) {
            throw new ValidationException("user can not be null");
        }
        UserDto dto = new UserDto();
        dto.setId(user.getId());
        dto.setName(user.getName());
        dto.setEmail(user.getEmail());
        return dto;
    }

    public static User toUserFromNewUserRequest(NewUserRequest newUserRequest) {
        if (newUserRequest == null) {
            throw new ValidationException("newUserRequest can not be null");
        }
        User user = new User();
        user.setName(newUserRequest.getName());
        user.setEmail(newUserRequest.getEmail());
        return user;
    }
}
