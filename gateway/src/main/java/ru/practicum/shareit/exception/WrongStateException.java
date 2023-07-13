package ru.practicum.shareit.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(code = HttpStatus.BAD_REQUEST, reason = "Unknown state: UNSUPPORTED_STATUS")
public class WrongStateException extends RuntimeException {
    public WrongStateException(String s) {
        super(s);
    }
}
