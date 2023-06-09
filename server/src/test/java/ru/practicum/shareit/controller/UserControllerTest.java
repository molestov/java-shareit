package ru.practicum.shareit.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.json.JsonMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mapstruct.factory.Mappers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import ru.practicum.shareit.error.exception.DuplicatedEmailException;
import ru.practicum.shareit.error.exception.EmptyEmailException;
import ru.practicum.shareit.error.exception.EmptyNameException;
import ru.practicum.shareit.error.exception.IllegalUserException;
import ru.practicum.shareit.user.controller.UserController;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.mapper.UserMapper;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.service.UserService;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;

import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
public class UserControllerTest {
    @InjectMocks
    private UserController userController;

    @Spy
    private UserMapper userMapper = Mappers.getMapper(UserMapper.class);

    @Mock
    private UserService userService;

    private final ObjectMapper mapper = JsonMapper.builder()
            .addModule(new JavaTimeModule())
            .build();

    private MockMvc mvc;

    @BeforeEach
    void setUp() {
        mvc = MockMvcBuilders
                .standaloneSetup(userController)
                .build();
    }

    @Test
    void testAddUser() throws Exception {
        when(userService.addUser(any(User.class)))
                .thenReturn(createUser());

        mvc.perform(post("/users")
                        .content(mapper.writeValueAsString(createUserDto()))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))

                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(createUserDto().getId()), Long.class));
        verify(userService, times(1)).addUser(any(User.class));
    }

    @Test
    void testAddUserWithEmptyName() throws Exception {
        when(userService.addUser(any(User.class)))
                .thenThrow(new EmptyNameException("Example"));

        mvc.perform(post("/users")
                        .content(mapper.writeValueAsString(createUserDto()))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))

                .andExpect(status().isBadRequest());
        verify(userService, times(1)).addUser(any(User.class));
    }

    @Test
    void testAddUserWithEmptyEmail() throws Exception {
        when(userService.addUser(any(User.class)))
                .thenThrow(new EmptyEmailException("Example"));

        mvc.perform(post("/users")
                        .content(mapper.writeValueAsString(createUserDto()))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))

                .andExpect(status().isBadRequest());
        verify(userService, times(1)).addUser(any(User.class));
    }

    @Test
    void testAddUserWithDuplicateEmail() throws Exception {
        when(userService.addUser(any(User.class)))
                .thenThrow(new DuplicatedEmailException("Example"));

        mvc.perform(post("/users")
                        .content(mapper.writeValueAsString(createUserDto()))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))

                .andExpect(status().isConflict());
        verify(userService, times(1)).addUser(any(User.class));
    }

    @Test
    void testUpdateUser() throws Exception {
        when(userService.updateUser(anyLong(), any(UserDto.class)))
                .thenReturn(createUser());

        mvc.perform(patch("/users/1")
                        .content(mapper.writeValueAsString(createUserDto()))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))

                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(createUserDto().getId()), Long.class));
        verify(userService, times(1)).updateUser(anyLong(), any(UserDto.class));
    }

    @Test
    void testUpdateUserWithError() throws Exception {
        when(userService.updateUser(anyLong(), any(UserDto.class)))
                .thenThrow(new IllegalUserException("Example"));

        mvc.perform(patch("/users/1")
                        .content(mapper.writeValueAsString(createUserDto()))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))

                .andExpect(status().isNotFound());
        verify(userService, times(1)).updateUser(anyLong(), any(UserDto.class));
    }

    @Test
    void testGetUser() throws Exception {
        when(userService.getUser(anyLong()))
                .thenReturn(createUser());

        mvc.perform(get("/users/1")
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))

                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(createUserDto().getId()), Long.class));
        verify(userService, times(1)).getUser(anyLong());
    }

    @Test
    void testGetAllUsers() throws Exception {
        when(userService.getAllUsers())
                .thenReturn(new ArrayList<User>());

        mvc.perform(get("/users")
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))

                .andExpect(status().isOk());
        verify(userService, times(1)).getAllUsers();
    }

    @Test
    void testDeleteUser() throws Exception {
        mvc.perform(delete("/users/1")
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))

                .andExpect(status().isOk());
        verify(userService, times(1)).removeUser(anyLong());
    }

    private User createUser() {
        User user = new User();
        user.setId(1L);
        user.setName("Example");
        user.setEmail("example@example.com");
        return user;
    }

    private UserDto createUserDto() {
        UserDto userDto = new UserDto();
        userDto.setId(1L);
        userDto.setName("Example");
        userDto.setEmail("example@example.com");
        return userDto;
    }
}
