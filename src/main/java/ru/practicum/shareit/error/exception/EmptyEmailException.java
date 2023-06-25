package ru.practicum.shareit.error.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(code = HttpStatus.BAD_REQUEST, reason = "Email cannot be empty")
public class EmptyEmailException extends RuntimeException{
}
