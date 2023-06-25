package ru.practicum.shareit.booking.service;

import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.error.exception.EndBeforeStartException;
import ru.practicum.shareit.error.exception.UnavailableItemException;
import ru.practicum.shareit.error.exception.WrongStateException;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingState;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.booking.storage.BookingStorage;
import ru.practicum.shareit.error.exception.BookingByOwnerException;
import ru.practicum.shareit.error.exception.IllegalUserException;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.storage.ItemStorage;
import ru.practicum.shareit.error.exception.UnknownIdException;
import ru.practicum.shareit.user.storage.UserStorage;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Service
@AllArgsConstructor
public class BookingService {
    @Autowired
    private final BookingStorage bookingStorage;
    @Autowired
    private final UserStorage userStorage;
    @Autowired
    private final ItemStorage itemStorage;

    public Booking addBooking(Long id, Booking booking) {
        if (!booking.getItem().getAvailable()) {
            throw new UnavailableItemException();
        }
        if (booking.getStart().after(booking.getEnd()) || booking.getStart().equals(booking.getEnd())) {
            throw new EndBeforeStartException();
        }
        if (Objects.equals(itemStorage.findById(booking.getItem().getId()).orElseThrow(UnknownIdException::new)
                .getOwner(), id)) {
            throw new BookingByOwnerException();
        }
        booking.setStatus(BookingStatus.WAITING);
        return bookingStorage.save(booking);
    }

    public Booking getBooking(Long userId, Long bookingId) {
        Booking booking = bookingStorage.findById(bookingId).orElseThrow(UnknownIdException::new);
        if (!Objects.equals(userId, booking.getBooker().getId())) {
            Item item = itemStorage.findById(booking.getItem().getId()).orElseThrow(UnknownIdException::new);
            if (!Objects.equals(userId, item.getOwner())) {
                throw new IllegalUserException();
            }
        }
        return booking;
    }

    public Booking setBookingStatus(Long userId, Long bookingId, boolean approved) {
        Booking booking = bookingStorage.findById(bookingId).orElseThrow(UnknownIdException::new);
        if (!userStorage.existsById(userId)) {
            throw new UnknownIdException();
        }
        Item item = itemStorage.findById(booking.getItem().getId()).orElseThrow(UnknownIdException::new);
        if (!userId.equals(item.getOwner())) {
            throw new IllegalUserException();
        }
        if (approved) {
            if (!booking.getStatus().equals(BookingStatus.APPROVED)) {
                booking.setStatus(BookingStatus.APPROVED);
            } else {
                throw new UnavailableItemException();
            }
        } else {
            booking.setStatus(BookingStatus.REJECTED);
        }
        return bookingStorage.save(booking);
    }

    public List<Booking> getAllUserBookingsByState(Long userId, String state) {
        BookingState bookingState;
        try {
            bookingState = BookingState.valueOf(state);
        } catch (Exception e) {
            throw new WrongStateException("Unknown state: " + state, e);
        }
        if (!userStorage.existsById(userId)) {
            throw new UnknownIdException();
        }
        List<Booking> result = new ArrayList<>();
        switch (bookingState) {
            case ALL:
                result = bookingStorage.getAllBookingsByBookerId(userId);
                break;

            case FUTURE:
                result = bookingStorage.getAllFutureBookingsForBooker(userId);
                break;

            case CURRENT:
                result = bookingStorage.getAllCurrentBookingsForBooker(userId);
                break;

            case PAST:
                result = bookingStorage.getAllPastBookingsForBooker(userId);
                break;

            case WAITING:
                result = bookingStorage.getAllWaitingBookingsForBooker(userId);
                break;

            case REJECTED:
                result = bookingStorage.getAllRejectedBookingsForBooker(userId);
                break;
        }
        return result;
    }

    public List<Booking> getAllOwnerBookingsByState(Long userId, String state) {
        BookingState bookingState;
        try {
            bookingState = BookingState.valueOf(state);
        } catch (Exception e) {
            throw new WrongStateException("Unknown state: " + state, e);
        }
        if (!userStorage.existsById(userId)) {
            throw new UnknownIdException();
        }
        List<Booking> result = new ArrayList<>();
        switch (bookingState) {
            case ALL:
                result = bookingStorage.getAllBookingsByOwnerId(userId);
                break;

            case FUTURE:
                result = bookingStorage.getAllFutureBookingsForOwner(userId);
                break;

            case CURRENT:
                result = bookingStorage.getAllCurrentBookingsForOwner(userId);
                break;

            case PAST:
                result = bookingStorage.getAllPastBookingsForOwner(userId);
                break;

            case WAITING:
                result = bookingStorage.getAllWaitingBookingsForOwner(userId);
                break;

            case REJECTED:
                result = bookingStorage.getAllRejectedBookingsForOwner(userId);
                break;
        }
        return result;
    }
}
