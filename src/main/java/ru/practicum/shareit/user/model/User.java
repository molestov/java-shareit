package ru.practicum.shareit.user.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NonNull;

import javax.validation.constraints.Email;

@Data
@AllArgsConstructor
public class User {
    private Long id;
    private String name;
    @Email
    @NonNull
    private String email;
}
