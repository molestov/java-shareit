package ru.practicum.shareit.user.service;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.BeanWrapper;
import org.springframework.beans.BeanWrapperImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.user.exception.DuplicatedEmailException;
import ru.practicum.shareit.user.exception.UnknownIdException;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.storage.UserStorage;

import java.beans.FeatureDescriptor;
import java.util.List;
import java.util.stream.Stream;

@Service
@EnableJpaRepositories
public class UserService {
    private final UserStorage userStorage;

    @Autowired
    public UserService(UserStorage userStorage) {
        this.userStorage = userStorage;
    }

    public User addUser(User user) {
        if (userStorage.findByEmail(user.getEmail()) != null) {
            throw new DuplicatedEmailException();
        }
        return userStorage.save(user);
    }

    public User updateUser(Long id, UserDto user) {
        User userFromDb = userStorage.findById(id).get();
        if (user.getEmail() != null
                && !user.getEmail().equals(userFromDb.getEmail())
                &&  userStorage.findByEmail(user.getEmail()) != null) {
            throw new DuplicatedEmailException();
        }
        String[] ignoredProperties = getNullPropertyNames(user);
        BeanUtils.copyProperties(user, userFromDb, ignoredProperties);
        return userStorage.save(userFromDb);
    }

    private static String[] getNullPropertyNames(Object object) {
        final BeanWrapper wrappedSource = new BeanWrapperImpl(object);
        return Stream.of(wrappedSource.getPropertyDescriptors())
                .map(FeatureDescriptor::getName)
                .filter(propertyName -> wrappedSource.getPropertyValue(propertyName) == null)
                .toArray(String[]::new);
    }

    public User getUser(Long id) {
        if (!userStorage.existsById(id)) {
            throw new UnknownIdException();
        }
        return userStorage.findById(id).get();
    }

    public void removeUser(Long userId) {
        userStorage.deleteById(userId);
    }

    public List<User> getAllUsers() {
        return userStorage.findAll();
    }
}
