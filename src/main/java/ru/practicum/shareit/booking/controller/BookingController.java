package ru.practicum.shareit.booking.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import ru.practicum.shareit.OffsetBasedPageRequest;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingDtoWithEntities;
import ru.practicum.shareit.booking.mapper.BookingMapper;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.service.BookingService;
import ru.practicum.shareit.error.exception.WrongStateException;
import ru.practicum.shareit.item.service.ItemService;
import ru.practicum.shareit.user.service.UserService;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping(path = "/bookings")
public class BookingController {

    @Autowired
    private final BookingService bookingService;

    @Autowired
    private final BookingMapper bookingMapper;

    @Autowired
    private final UserService userService;

    @Autowired
    private final ItemService itemService;

    @Autowired
    public BookingController(BookingService bookingService, BookingMapper bookingMapper,
                             UserService userService, ItemService itemService) {
        this.bookingService = bookingService;
        this.bookingMapper = bookingMapper;
        this.userService = userService;
        this.itemService = itemService;
    }

    @PostMapping
    public BookingDtoWithEntities addBooking(@RequestHeader("X-Sharer-User-Id") Long id,
                                             @Valid @RequestBody BookingDto bookingDto) {
        bookingDto.setBookerId(id);
        Booking booking = bookingMapper.bookingDtoToBooking(bookingDto);
        booking.setItem(itemService.getItemById(id, booking.getItem().getId()));
        booking.setBooker(userService.getUser(booking.getBooker().getId()));
        return bookingMapper.bookingToBookingDtoWithEntities(bookingService.addBooking(id, booking));
    }

    @GetMapping("/{bookingId}")
    public BookingDtoWithEntities getBooking(@RequestHeader("X-Sharer-User-Id") Long userId,
                                             @PathVariable Long bookingId) {
        return bookingMapper.bookingToBookingDtoWithEntities(bookingService.getBooking(userId, bookingId));
    }

    @PatchMapping("/{bookingId}")
    public BookingDtoWithEntities setBookingStatus(@RequestHeader("X-Sharer-User-Id") Long userId,
                                       @PathVariable Long bookingId,
                                       @RequestParam(value = "approved") boolean approved) {
        return bookingMapper.bookingToBookingDtoWithEntities(
                bookingService.setBookingStatus(userId, bookingId, approved));
    }

    @GetMapping
    public List<BookingDtoWithEntities> getAllUserBookingsByState(@RequestHeader("X-Sharer-User-Id") Long userId,
                                         @RequestParam(value = "state", defaultValue = "ALL") String state,
                                         @RequestParam(value = "from", defaultValue = "0") int from,
                                         @RequestParam(value = "size", defaultValue = "9999") int size) {
        if (from < 0) {
            throw new WrongStateException("From cannot be less then 0");
        }
        if (size < 1) {
            throw new WrongStateException("Size cannot be less then 1");
        }
        return bookingMapper.toListDtoWithEntities(bookingService.getAllUserBookingsByState(userId, state,
                new OffsetBasedPageRequest(from, size)));
    }

    @GetMapping("/owner")
    public List<BookingDtoWithEntities> getAllOwnerBookingsByState(@RequestHeader("X-Sharer-User-Id") Long userId,
                                                @RequestParam(value = "state", defaultValue = "ALL") String state,
                                                @RequestParam(value = "from", defaultValue = "0") int from,
                                                @RequestParam(value = "size", defaultValue = "9999") int size) {
        if (from < 0) {
            throw new WrongStateException("From cannot be less then 0");
        }
        if (size < 1) {
            throw new WrongStateException("Size cannot be less then 1");
        }
        return bookingMapper.toListDtoWithEntities(bookingService.getAllOwnerBookingsByState(userId, state,
                new OffsetBasedPageRequest(from, size)));
    }
}
