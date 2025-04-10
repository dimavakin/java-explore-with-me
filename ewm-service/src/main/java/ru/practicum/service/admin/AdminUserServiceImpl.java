package ru.practicum.service.admin;

import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.exception.DuplicatedDataException;
import ru.practicum.exception.NotFoundException;
import ru.practicum.model.User;
import ru.practicum.mapper.UserMapper;
import ru.practicum.repository.UserRepository;
import ru.practicum.dto.user.NewUserRequest;
import ru.practicum.dto.user.UserDto;

import java.util.List;

@Service
@RequiredArgsConstructor
public class AdminUserServiceImpl implements AdminUserService {
    private final UserRepository userRepository;

    @Override
    public List<UserDto> getUsers(List<Long> ids, Integer from, Integer size) {
        Pageable pageable = PageRequest.of(from / size, size);
        Page<User> users;
        if (ids == null || ids.isEmpty()) {
            users = userRepository.findAll(pageable);
        } else {
            users = userRepository.findByIdIn(ids, pageable);
        }
        return users.stream().map(UserMapper::toUserDtoFromUser).toList();
    }

    @Transactional
    @Override
    public UserDto postUsers(NewUserRequest newUserRequest) {
        try {
            User user = userRepository.saveAndFlush(UserMapper.toUserFromNewUserRequest(newUserRequest));
            return UserMapper.toUserDtoFromUser(user);
        } catch (DataIntegrityViolationException e) {
            throw new DuplicatedDataException("Category with this name already exists");
        }
    }

    @Transactional
    @Override
    public void deleteUser(Long userId) {
        if (userRepository.existsById(userId)) {
            userRepository.deleteById(userId);
        } else {
            throw new NotFoundException(String.format("User with id=%d was not found", userId));
        }
    }
}
