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
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingDtoWithEntities;
import ru.practicum.shareit.booking.mapper.BookingMapper;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.service.BookingService;
import ru.practicum.shareit.item.service.ItemService;
import ru.practicum.shareit.user.service.UserService;

import javax.validation.Valid;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping(path = "/bookings")
public class BookingController {
    private final BookingService bookingService;
    private final BookingMapper bookingMapper;
    private final UserService userService;
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
                                         @RequestParam(value = "state", defaultValue = "ALL") String state) {
        return bookingService.getAllUserBookingsByState(userId, state).stream()
                .map(bookingMapper::bookingToBookingDtoWithEntities)
                .collect(Collectors.toList());
    }

    @GetMapping("/owner")
    public List<BookingDtoWithEntities> getAllOwnerBookingsByState(@RequestHeader("X-Sharer-User-Id") Long userId,
                                                      @RequestParam(value = "state", defaultValue = "ALL") String state) {
        return bookingService.getAllOwnerBookingsByState(userId, state).stream()
                .map(bookingMapper::bookingToBookingDtoWithEntities)
                .collect(Collectors.toList());
    }
}
