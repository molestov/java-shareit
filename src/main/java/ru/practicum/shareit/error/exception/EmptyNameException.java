package ru.practicum.shareit.error.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(code = HttpStatus.BAD_REQUEST, reason = "Name field cannot be empty")
public class EmptyNameException extends RuntimeException {
    public EmptyNameException(String s) {
        super(s);
    }
}
