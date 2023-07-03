package ru.practicum.shareit.service;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.practicum.shareit.error.exception.DuplicatedEmailException;
import ru.practicum.shareit.error.exception.UnknownIdException;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;
import ru.practicum.shareit.user.service.UserService;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import static org.mockito.ArgumentMatchers.any;

@ExtendWith(MockitoExtension.class)
public class UserServiceTest {
    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private UserService userService;

    @BeforeEach
    void setup() {
    }

    @Test
    void testAddUser() {
        when(userRepository.findByEmail(anyString()))
                .thenReturn(Optional.empty());
        when(userRepository.save(any(User.class)))
                .thenReturn(createUser());

        User savedUser = userService.addUser(createUser());

        Assertions.assertEquals(1L, savedUser.getId());
    }

    @Test
    void testAddUserWithError() {
        when(userRepository.findByEmail(anyString()))
                .thenReturn(Optional.of(createUser()));

        DuplicatedEmailException exception = assertThrows(DuplicatedEmailException.class,
                () -> userService.addUser(createUser()));

        assertTrue(exception.getMessage().contains("Email already registered"));
    }

    @Test
    void testUpdateUser() {
        UserDto userDto = new UserDto();
        userDto.setName("Example");
        when(userRepository.findById(anyLong()))
                .thenReturn(Optional.of(createUser()));
        when(userRepository.save(any(User.class)))
                .thenReturn(createUser());

        User savedUser = userService.updateUser(1L, userDto);

        Assertions.assertEquals(1L, savedUser.getId());
    }

    @Test
    void testUpdateUserWithError() {
        UserDto userDto = new UserDto();
        when(userRepository.findById(anyLong()))
                .thenReturn(Optional.empty());

        UnknownIdException exception = assertThrows(UnknownIdException.class,
                () -> userService.updateUser(1L, userDto));

        assertTrue(exception.getClass().equals(UnknownIdException.class));
    }

    @Test
    void testUpdateUserWithError2() {
        UserDto userDto = new UserDto();
        userDto.setEmail("another@another.com");
        when(userRepository.findById(anyLong()))
                .thenReturn(Optional.of(createUser()));
        when(userRepository.findByEmail(anyString()))
                .thenReturn(Optional.of(createUser()));

        DuplicatedEmailException exception = assertThrows(DuplicatedEmailException.class,
                () -> userService.updateUser(1L, userDto));

        assertEquals("Email already registered", exception.getMessage());
    }

    @Test
    void testGetUser() {
        when(userRepository.existsById(anyLong()))
                .thenReturn(true);
        when(userRepository.findById(anyLong()))
                .thenReturn(Optional.of(createUser()));

        User savedUser = userService.getUser(1L);

        Assertions.assertEquals(1L, savedUser.getId());
    }

    @Test
    void testGetUserWithError() {
        when(userRepository.existsById(anyLong()))
                .thenReturn(false);

        UnknownIdException exception = assertThrows(UnknownIdException.class,
                () -> userService.getUser(1L));

        assertTrue(exception.getClass().equals(UnknownIdException.class));
    }

    @Test
    void testGetUserWithError2() {
        when(userRepository.existsById(anyLong()))
                .thenReturn(true);
        when(userRepository.findById(anyLong()))
                .thenReturn(Optional.empty());

        UnknownIdException exception = assertThrows(UnknownIdException.class,
                () -> userService.getUser(1L));

        assertTrue(exception.getClass().equals(UnknownIdException.class));
    }

    @Test
    void testGetAllUsers() {
        when(userRepository.findAll())
                .thenReturn(new ArrayList<>());

        List<User> savedUser = userService.getAllUsers();

        Assertions.assertNotNull(savedUser);
    }

    protected User createUser() {
        User user = new User();
        user.setId(1L);
        user.setName("Example");
        user.setEmail("examle@example.com");
        return user;
    }
}
