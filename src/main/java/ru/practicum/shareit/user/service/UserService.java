package ru.practicum.shareit.user.service;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.BeanWrapper;
import org.springframework.beans.BeanWrapperImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.error.exception.DuplicatedEmailException;
import ru.practicum.shareit.error.exception.UnknownIdException;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.beans.FeatureDescriptor;
import java.util.List;
import java.util.stream.Stream;

@Service
@EnableJpaRepositories
public class UserService {
    private final UserRepository userRepository;

    @Autowired
    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public User addUser(User user) {
        if (userRepository.findByEmail(user.getEmail()).isPresent()) {
            throw new DuplicatedEmailException("Email already registered");
        }
        return userRepository.save(user);
    }

    public User updateUser(Long id, UserDto user) {
        User userFromDb = userRepository.findById(id).orElseThrow(UnknownIdException::new);
        if (user.getEmail() != null
                && !user.getEmail().equals(userFromDb.getEmail())
                && userRepository.findByEmail(user.getEmail()).isPresent()) {
            throw new DuplicatedEmailException("Email already registered");
        }
        String[] ignoredProperties = getNullPropertyNames(user);
        BeanUtils.copyProperties(user, userFromDb, ignoredProperties);
        return userRepository.save(userFromDb);
    }

    private static String[] getNullPropertyNames(Object object) {
        final BeanWrapper wrappedSource = new BeanWrapperImpl(object);
        return Stream.of(wrappedSource.getPropertyDescriptors())
                .map(FeatureDescriptor::getName)
                .filter(propertyName -> wrappedSource.getPropertyValue(propertyName) == null)
                .toArray(String[]::new);
    }

    public User getUser(Long id) {
        if (!userRepository.existsById(id)) {
            throw new UnknownIdException("Id not found");
        }
        return userRepository.findById(id).orElseThrow(UnknownIdException::new);
    }

    public void removeUser(Long userId) {
        userRepository.deleteById(userId);
    }

    public List<User> getAllUsers() {
        return userRepository.findAll();
    }
}
