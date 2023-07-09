package ru.practicum.shareit.error.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(code = HttpStatus.BAD_REQUEST, reason = "Item start date is after item end date")
public class EndBeforeStartException extends RuntimeException {
    public EndBeforeStartException(String s) {
        super(s);
    }
}
