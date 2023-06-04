package ru.practicum.shareit.user.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;

import javax.validation.constraints.Email;

@Data
@AllArgsConstructor
public class UserDto {
    @JsonProperty("id")
    private Long id;
    @JsonProperty("name")
    private String name;
    @Email
    @JsonProperty("email")
    private String email;
}
