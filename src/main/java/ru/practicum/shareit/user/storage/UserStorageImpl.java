package ru.practicum.shareit.user.storage;

import org.springframework.stereotype.Component;
import ru.practicum.shareit.user.model.User;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Component
public class UserStorageImpl implements UserStorage {
    private final Map<Long, User> users = new HashMap<>();

    @Override
    public User addUser(User user) {
        users.put(user.getId(), user);
        return users.get(user.getId());
    }

    @Override
    public void removeUser(Long userId) {
        users.remove(userId);
    }

    @Override
    public User updateUser(Long id, User user) {
        users.replace(id, user);
        return users.get(id);
    }

    @Override
    public User getUser(Long userId) {
        return users.get(userId);
    }

    @Override
    public Boolean checkEmail(String email) {
        for (User user : users.values()) {
            if (user.getEmail().equals(email)) {
                return true;
            }
        }
        return false;
    }

    @Override
    public Boolean checkUserId(Long userId) {
        return users.containsKey(userId);
    }

    @Override
    public List<User> getAllUsers() {
        return new ArrayList<>(users.values());
    }
}
