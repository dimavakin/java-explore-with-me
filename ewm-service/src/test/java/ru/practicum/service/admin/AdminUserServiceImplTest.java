package ru.practicum.service.admin;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import ru.practicum.dto.user.NewUserRequest;
import ru.practicum.dto.user.UserDto;
import ru.practicum.mapper.UserMapper;
import ru.practicum.model.User;
import ru.practicum.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;


import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AdminUserServiceImplTest {
    @Mock
    private UserRepository userRepository;
    @Mock
    private UserMapper userMapper;
    @InjectMocks
    private AdminUserServiceImpl adminUserService;

    private User user;
    private User user2;

    @BeforeEach
    void setUp() {
        user = new User(1L, "John Doe", "john.doe@example.com");
        user2 = new User(2L, "John Doe2", "john.doe@example.com2");
        UserDto userDto = new UserDto(1L, "John Doe", "john@example.com");
        UserDto userDto2 = new UserDto(2L, "John Doe2", "john.doe@example.com2");
    }

    @Test
    void shouldReturnAllUsersWhenIdsEmpty() {
        int from = 0;
        int size = 10;
        Pageable pageable = PageRequest.of(from / size, size);
        Page<User> page = new PageImpl<>(List.of(user));

        when(userRepository.findAll(pageable)).thenReturn(page);

        List<UserDto> result = adminUserService.getUsers(Collections.emptyList(), from, size);

        assertEquals(1, result.size());
        assertEquals("John Doe", result.get(0).getName());

        verify(userRepository).findAll(pageable);
        verify(userRepository, never()).findByIdIn(anyList(), any());
    }

    @Test
    void shouldReturnUsersByIdsWhenIdsNotEmpty() {
        int from = 0;
        int size = 10;
        List<Long> ids = List.of(1L, 2L);
        Pageable pageable = PageRequest.of(from / size, size);
        Page<User> page = new PageImpl<>(List.of(user, user2));

        when(userRepository.findByIdIn(ids, pageable)).thenReturn(page);

        List<UserDto> result = adminUserService.getUsers(ids, from, size);

        assertEquals(2, result.size());
        assertEquals("John Doe", result.get(0).getName());
        assertEquals("John Doe2", result.get(1).getName());

        verify(userRepository).findByIdIn(ids, pageable);
        verify(userRepository, never()).findAll(any(Pageable.class));
    }

    @Test
    void shouldCreateNewUser() {
        // Подготовка тестовых данных
        NewUserRequest request = new NewUserRequest("John Doe", "john@example.com");
        User user = new User(null, "John Doe", "john@example.com");
        User savedUser = new User(1L, "John Doe", "john@example.com");
        UserDto expectedDto = new UserDto(1L, "John Doe", "john@example.com");

        // Мокаем статический метод UserMapper
        try (MockedStatic<UserMapper> mocked = mockStatic(UserMapper.class)) {
            // Мокаем поведение для метода toUserFromNewUserRequest
            mocked.when(() -> UserMapper.toUserFromNewUserRequest(request)).thenReturn(user);
            // Мокаем поведение для метода toUserDtoFromUser
            mocked.when(() -> UserMapper.toUserDtoFromUser(savedUser)).thenReturn(expectedDto);

            // Мокаем поведение для save
            when(userRepository.save(user)).thenReturn(savedUser);

            // Выполнение теста
            UserDto actualDto = adminUserService.postUsers(request);

            // Проверка
            assertEquals(expectedDto, actualDto);
            verify(userRepository).save(user);  // Проверка, что save был вызван
        }
    }
    @Test
    void shouldDeleteUserWhenExists() {
        Long userId = 1L;
        when(userRepository.existsById(userId)).thenReturn(true);

        adminUserService.deleteUser(userId);

        verify(userRepository).deleteById(userId);
    }
}
