package ru.practicum.service.admin;

import ru.practicum.dto.user.NewUserRequest;
import ru.practicum.dto.user.UserDto;

import java.util.List;

public interface AdminUserService {

    List<UserDto> getUsers(List<Long> ids, Integer from, Integer size);


    UserDto postUsers(NewUserRequest newUserRequest);


    void deleteUser(Long userId);
}
