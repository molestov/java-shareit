package ru.practicum.shareit;

import org.junit.jupiter.api.Test;
import ru.practicum.shareit.error.ErrorHandler;
import ru.practicum.shareit.error.exception.BookingByOwnerException;
import ru.practicum.shareit.error.exception.DuplicatedEmailException;
import ru.practicum.shareit.error.exception.IllegalUserException;
import ru.practicum.shareit.error.exception.UnknownIdException;
import ru.practicum.shareit.error.model.ErrorResponse;

import static org.junit.jupiter.api.Assertions.assertTrue;

public class ErrorHandlerTest {
    ErrorHandler errorHandler = new ErrorHandler();

    @Test
    void testHandleThrowable() {
        ErrorResponse response = errorHandler.handleThrowable(new Throwable());
        assertTrue(response.getClass().equals(ErrorResponse.class));
    }

    @Test
    void testHandleUnknownId() {
        ErrorResponse response = errorHandler.handleUnknownId(new UnknownIdException());
        assertTrue(response.getClass().equals(ErrorResponse.class));
    }

    @Test
    void testHandleDuplicateEmail() {
        ErrorResponse response = errorHandler.handleDuplicateEmail(new DuplicatedEmailException("Example"));
        assertTrue(response.getClass().equals(ErrorResponse.class));
        assertTrue(response.getError().contains("Example"));
    }

    @Test
    void testHandleIllegalUser() {
        ErrorResponse response = errorHandler.handleIllegalUser(new IllegalUserException("Example"));
        assertTrue(response.getClass().equals(ErrorResponse.class));
        assertTrue(response.getError().contains("Example"));
    }

    @Test
    void testHandleOwnerBooking() {
        ErrorResponse response = errorHandler.handleOwnerBooking(new BookingByOwnerException("Example"));
        assertTrue(response.getClass().equals(ErrorResponse.class));
        assertTrue(response.getError().contains("Example"));
    }
}
