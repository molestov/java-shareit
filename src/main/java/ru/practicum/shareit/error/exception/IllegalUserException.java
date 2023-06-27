package ru.practicum.shareit.error.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(code = HttpStatus.NOT_FOUND, reason = "Wrong owner id provided")
public class IllegalUserException extends RuntimeException {
    public IllegalUserException(String s) {
        super(s);
    }
}