package ru.practicum.shareit.user.controller;

import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.mapper.UserMapper;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.service.UserService;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@AllArgsConstructor
@RequestMapping(path = "/users")
public class UserController {
    private final UserService userService;
    private final UserMapper userMapper;

    @PostMapping
    public UserDto addUser(@RequestBody UserDto user) {
        User userEntity = userMapper.toUser(user);
        return userMapper.toUserDto(userService.addUser(userEntity));
    }

    @PatchMapping("/{id}")
    public UserDto updateUser(@PathVariable Long id, @RequestBody UserDto user) {
        return userMapper.toUserDto(userService.updateUser(id, user));
    }

    @GetMapping("/{id}")
    public UserDto getUser(@PathVariable Long id) {
        return userMapper.toUserDto(userService.getUser(id));
    }

    @GetMapping
    public List<UserDto> getAllUsers() {
        List<User> users =  userService.getAllUsers();
        return users.stream()
                .map(userMapper::toUserDto)
                .collect(Collectors.toList());
    }

    @DeleteMapping("/{id}")
    public void removeUser(@PathVariable Long id) {
        userService.removeUser(id);
    }

}
