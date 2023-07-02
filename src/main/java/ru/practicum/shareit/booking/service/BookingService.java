package ru.practicum.shareit.booking.service;

import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingState;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.error.exception.BookingByOwnerException;
import ru.practicum.shareit.error.exception.EndBeforeStartException;
import ru.practicum.shareit.error.exception.IllegalUserException;
import ru.practicum.shareit.error.exception.UnavailableItemException;
import ru.practicum.shareit.error.exception.UnknownIdException;
import ru.practicum.shareit.error.exception.WrongStateException;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.user.repository.UserRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

import static ru.practicum.shareit.booking.repository.BookingSpecification.endAfterNow;
import static ru.practicum.shareit.booking.repository.BookingSpecification.endBeforeNow;
import static ru.practicum.shareit.booking.repository.BookingSpecification.hasBookerId;
import static ru.practicum.shareit.booking.repository.BookingSpecification.hasItemId;
import static ru.practicum.shareit.booking.repository.BookingSpecification.hasOwnerId;
import static ru.practicum.shareit.booking.repository.BookingSpecification.hasStatus;
import static ru.practicum.shareit.booking.repository.BookingSpecification.orderByStartDateAsc;
import static ru.practicum.shareit.booking.repository.BookingSpecification.orderByStartDateDesc;
import static ru.practicum.shareit.booking.repository.BookingSpecification.startAfterNow;
import static ru.practicum.shareit.booking.repository.BookingSpecification.startBeforeNow;

@Service
@AllArgsConstructor
public class BookingService {
    @Autowired
    private final BookingRepository bookingRepository;

    @Autowired
    private final UserRepository userRepository;

    @Autowired
    private final ItemRepository itemRepository;

    public Booking addBooking(Long id, Booking booking) {
        if (!booking.getItem().getAvailable()) {
            throw new UnavailableItemException("Item is not available");
        }
        if (booking.getStart().after(booking.getEnd()) || booking.getStart().equals(booking.getEnd())) {
            throw new EndBeforeStartException("Incorrect end date provided");
        }
        if (Objects.equals(itemRepository.findById(booking.getItem().getId()).orElseThrow(UnknownIdException::new)
                .getOwner().getId(), id)) {
            throw new BookingByOwnerException("Booking by owner attempt");
        }
        booking.setStatus(BookingStatus.WAITING);
        return bookingRepository.save(booking);
    }

    public Booking getBooking(Long userId, Long bookingId) {
        Booking booking = bookingRepository.findById(bookingId).orElseThrow(UnknownIdException::new);
        if (!Objects.equals(userId, booking.getBooker().getId())) {
            Item item = itemRepository.findById(booking.getItem().getId()).orElseThrow(UnknownIdException::new);
            if (!Objects.equals(userId, item.getOwner().getId())) {
                throw new IllegalUserException("Wrong user id provided");
            }
        }
        return booking;
    }

    public Booking setBookingStatus(Long userId, Long bookingId, boolean approved) {
        Booking booking = bookingRepository.findById(bookingId).orElseThrow(UnknownIdException::new);
        if (!userRepository.existsById(userId)) {
            throw new UnknownIdException("Id not found");
        }
        Item item = itemRepository.findById(booking.getItem().getId()).orElseThrow(UnknownIdException::new);
        if (!userId.equals(item.getOwner().getId())) {
            throw new IllegalUserException("Wrong user id provided");
        }
        if (approved) {
            if (!booking.getStatus().equals(BookingStatus.APPROVED)) {
                booking.setStatus(BookingStatus.APPROVED);
            } else {
                throw new UnavailableItemException("Booking already approved");
            }
        } else {
            booking.setStatus(BookingStatus.REJECTED);
        }
        return bookingRepository.save(booking);
    }

    public List<Booking> getAllUserBookingsByState(Long userId, String state, Pageable page) {
        BookingState bookingState;
        try {
            bookingState = BookingState.valueOf(state);
        } catch (Exception e) {
            throw new WrongStateException("Unknown state: " + state);
        }
        if (!userRepository.existsById(userId)) {
            throw new UnknownIdException("Id not found");
        }
        List<Booking> result = new ArrayList<>();
        switch (bookingState) {
            case ALL:
                result = bookingRepository.findAll(orderByStartDateDesc(hasBookerId(userId)), page).getContent();
                break;

            case FUTURE:
                result = bookingRepository.findAll(orderByStartDateDesc(hasBookerId(userId)).and(startAfterNow()),
                        page).getContent();
                break;

            case CURRENT:
                result = bookingRepository.findAll(orderByStartDateDesc(hasBookerId(userId).and(startBeforeNow()
                        .and(endAfterNow()))), page).getContent();
                break;

            case PAST:
                result = bookingRepository.findAll(orderByStartDateDesc(hasBookerId(userId).and(endBeforeNow())),
                        page).getContent();
                break;

            case WAITING:
                result = bookingRepository.findAll(hasBookerId(userId).and(hasStatus(BookingStatus.WAITING)),
                        page).getContent();
                break;

            case REJECTED:
                result = bookingRepository.findAll(hasBookerId(userId).and(hasStatus(BookingStatus.REJECTED)),
                        page).getContent();
                break;
        }
        return result;
    }

    public List<Booking> getAllOwnerBookingsByState(Long userId, String state, Pageable page) {
        BookingState bookingState;
        try {
            bookingState = BookingState.valueOf(state);
        } catch (Exception e) {
            throw new WrongStateException("Unknown state: " + state);
        }
        if (!userRepository.existsById(userId)) {
            throw new UnknownIdException("Id not found");
        }
        List<Booking> result = new ArrayList<>();
        switch (bookingState) {
            case ALL:
                result = bookingRepository.findAll(orderByStartDateDesc(hasOwnerId(userId)), page).getContent();
                break;

            case FUTURE:
                result = bookingRepository.findAll(orderByStartDateDesc(hasOwnerId(userId)).and(startAfterNow()),
                        page).getContent();
                break;

            case CURRENT:
                result = bookingRepository.findAll(orderByStartDateDesc(hasOwnerId(userId).and(startBeforeNow()
                        .and(endAfterNow()))), page).getContent();
                break;

            case PAST:
                result = bookingRepository.findAll(orderByStartDateDesc(hasOwnerId(userId).and(endBeforeNow())),
                        page).getContent();
                break;

            case WAITING:
                result = bookingRepository.findAll(hasOwnerId(userId).and(hasStatus(BookingStatus.WAITING)),
                        page).getContent();
                break;

            case REJECTED:
                result = bookingRepository.findAll(hasOwnerId(userId).and(hasStatus(BookingStatus.REJECTED)),
                        page).getContent();
                break;
        }
        return result;
    }

    public Optional<Booking> getLastBooking(Long itemId) {
        List<Booking> result = bookingRepository.findAll(orderByStartDateDesc(hasItemId(itemId).and(startBeforeNow())));
        if (!result.isEmpty()) {
            return Optional.of(result.get(0));
        }
        return Optional.empty();
    }

    public Optional<Booking> getNextBooking(Long itemId) {
        List<Booking> result = bookingRepository.findAll(orderByStartDateAsc(hasItemId(itemId).and(startAfterNow()
                .and(hasStatus(BookingStatus.APPROVED)))));
        if (!result.isEmpty()) {
            return Optional.of(result.get(0));
        }
        return Optional.empty();
    }
}
