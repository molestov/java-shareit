package ru.practicum.shareit.user;

import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.practicum.shareit.user.dto.UserDto;

import javax.validation.Valid;

@RestController
@AllArgsConstructor
@RequestMapping(path = "/users")
public class UserController {
    UserClient userClient;

    @PostMapping
    public ResponseEntity<Object> addUser(@RequestBody @Valid UserDto user) {
        return userClient.addUser(user);
    }

    @PatchMapping("/{id}")
    public ResponseEntity<Object> updateUser(@PathVariable Long id, @Valid @RequestBody UserDto user) {
        return userClient.updateUser(id, user);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Object> getUser(@PathVariable Long id) {
        return userClient.getUser(id);
    }

    @GetMapping
    public ResponseEntity<Object> getAllUsers() {
        return userClient.getAllUsers();
    }

    @DeleteMapping("/{id}")
    public void removeUser(@PathVariable Long id) {
        userClient.deleteUser(id);
    }

}
