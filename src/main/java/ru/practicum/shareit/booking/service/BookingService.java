package ru.practicum.shareit.booking.service;

import lombok.Data;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.booking.exception.EndBeforeStartException;
import ru.practicum.shareit.booking.exception.UnavailableItemException;
import ru.practicum.shareit.booking.exception.WrongStateException;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingState;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.booking.storage.BookingStorage;
import ru.practicum.shareit.item.exception.BookingByOwnerException;
import ru.practicum.shareit.item.exception.IllegalUserException;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.service.ItemService;
import ru.practicum.shareit.item.storage.ItemStorage;
import ru.practicum.shareit.user.exception.UnknownIdException;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.storage.UserStorage;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
@Data
public class BookingService {
    @Autowired
    private final BookingStorage bookingStorage;
    @Autowired
    private final UserStorage userStorage;
    @Autowired
    private final ItemStorage itemStorage;
    @Autowired
    private final ItemService itemService;

    public Booking addBooking(Long id, Booking booking) {
        if (!userStorage.existsById(id) || !itemStorage.existsById(booking.getItemId())) {
            throw new UnknownIdException();
        }
        if (!itemStorage.findById(booking.getItemId()).get().getAvailable()) {
            throw new UnavailableItemException();
        }
        if (booking.getStart().after(booking.getEnd()) || booking.getStart().equals(booking.getEnd())) {
            throw new EndBeforeStartException();
        }
        if (Objects.equals(itemStorage.findById(booking.getItemId()).get().getOwner(), id)) {
            throw new BookingByOwnerException();
        }
        booking.setBookerId(id);
        booking.setStatus(BookingStatus.WAITING);
        Booking savedBooking = bookingStorage.save(booking);
        return addEntitiesToReturnValue(savedBooking);
    }

    public Booking getBooking(Long userId, Long bookingId) {
        Booking booking = bookingStorage.findById(bookingId).orElseThrow(UnknownIdException::new);
        if (userId != booking.getBookerId()) {
            Item item = itemStorage.findById(booking.getItemId()).get();
            if (userId != item.getOwner()) {
                throw new IllegalUserException();
            }
        }
        return addEntitiesToReturnValue(booking);
    }

    public Booking setBookingStatus(Long userId, Long bookingId, boolean approved) {
        Booking booking = bookingStorage.findById(bookingId).get();
        if (!userStorage.existsById(userId) || !itemStorage.existsById(booking.getItemId())) {
            throw new UnknownIdException();
        }
        Item item = itemStorage.findById(booking.getItemId()).get();
        if (userId != item.getOwner()) {
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
        Booking savedBooking = bookingStorage.save(booking);
        return addEntitiesToReturnValue(savedBooking);
    }

    private Booking addEntitiesToReturnValue(Booking booking) {
        User user = userStorage.findById(booking.getBookerId()).get();
        Item item = itemStorage.findById(booking.getItemId()).get();
        booking.setBooker(user);
        booking.setItem(item);
        return booking;
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
                result = bookingStorage.getAllBookingsByBookerId(userId).stream()
                        .map(this::addEntitiesToReturnValue)
                        .collect(Collectors.toList());
                break;

            case FUTURE:
                result = bookingStorage.getAllFutureBookingsForBooker(userId).stream()
                        .map(this::addEntitiesToReturnValue)
                        .collect(Collectors.toList());
                break;

            case CURRENT:
                result = bookingStorage.getAllCurrentBookingsForBooker(userId).stream()
                        .map(this::addEntitiesToReturnValue)
                        .collect(Collectors.toList());
                break;

            case PAST:
                result = bookingStorage.getAllPastBookingsForBooker(userId).stream()
                        .map(this::addEntitiesToReturnValue)
                        .collect(Collectors.toList());
                break;

            case WAITING:
                result = bookingStorage.getAllWaitingBookingsForBooker(userId).stream()
                        .map(this::addEntitiesToReturnValue)
                        .collect(Collectors.toList());
                break;

            case REJECTED:
                result = bookingStorage.getAllRejectedBookingsForBooker(userId).stream()
                        .map(this::addEntitiesToReturnValue)
                        .collect(Collectors.toList());
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
                result = bookingStorage.getAllBookingsByOwnerId(userId).stream()
                        .map(this::addEntitiesToReturnValue)
                        .collect(Collectors.toList());
                break;

            case FUTURE:
                result = bookingStorage.getAllFutureBookingsForOwner(userId).stream()
                        .map(this::addEntitiesToReturnValue)
                        .collect(Collectors.toList());
                break;

            case CURRENT:
                result = bookingStorage.getAllCurrentBookingsForOwner(userId).stream()
                        .map(this::addEntitiesToReturnValue)
                        .collect(Collectors.toList());
                break;

            case PAST:
                result = bookingStorage.getAllPastBookingsForOwner(userId).stream()
                        .map(this::addEntitiesToReturnValue)
                        .collect(Collectors.toList());
                break;

            case WAITING:
                result = bookingStorage.getAllWaitingBookingsForOwner(userId).stream()
                        .map(this::addEntitiesToReturnValue)
                        .collect(Collectors.toList());
                break;

            case REJECTED:
                result = bookingStorage.getAllRejectedBookingsForOwner(userId).stream()
                        .map(this::addEntitiesToReturnValue)
                        .collect(Collectors.toList());
                break;
        }
        return result;
    }
}
