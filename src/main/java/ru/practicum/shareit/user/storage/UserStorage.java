package ru.practicum.shareit.user.storage;

import ru.practicum.shareit.user.model.User;

import java.util.List;

public interface UserStorage {
    User addUser(User user);
    void removeUser(Long userId);
    User updateUser(Long id, User user);
    User getUser(Long userId);
    Boolean checkEmail(String email);
    Boolean checkUserId(Long id);
    List<User> getAllUsers();
}
