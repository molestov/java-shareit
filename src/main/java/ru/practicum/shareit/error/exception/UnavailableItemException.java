package ru.practicum.shareit.error.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(code = HttpStatus.BAD_REQUEST, reason = "Item is not available for booking")
public class UnavailableItemException extends RuntimeException {
}
