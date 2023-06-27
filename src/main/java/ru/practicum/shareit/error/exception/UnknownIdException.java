package ru.practicum.shareit.error.exception;


import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(code = HttpStatus.NOT_FOUND, reason = "There is no such id")
public class UnknownIdException extends RuntimeException {
    public UnknownIdException(String s) {
        super(s);
    }

    public UnknownIdException() {
        super();
    }
}
