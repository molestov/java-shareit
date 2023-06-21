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
import ru.practicum.shareit.booking.exception.WrongStateException;
import ru.practicum.shareit.booking.mapper.BookingMapper;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingState;
import ru.practicum.shareit.booking.service.BookingService;

import javax.validation.Valid;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping(path = "/bookings")
public class BookingController {
    BookingService bookingService;
    BookingMapper bookingMapper;

    @Autowired
    public BookingController(BookingService bookingService, BookingMapper bookingMapper) {
        this.bookingService = bookingService;
        this.bookingMapper = bookingMapper;
    }

    @PostMapping
    public BookingDto addBooking(@RequestHeader("X-Sharer-User-Id") Long id,
                                 @Valid @RequestBody BookingDto bookingDto) {
        Booking booking = bookingMapper.bookingDtoToBooking(bookingDto);
        return bookingMapper.bookingToBookingDto(bookingService.addBooking(id, booking));
    }

    @GetMapping("/{bookingId}")
    public BookingDto getBooking(@RequestHeader("X-Sharer-User-Id") Long userId,
                                 @PathVariable Long bookingId) {
        return bookingMapper.bookingToBookingDto(bookingService.getBooking(userId, bookingId));
    }

    @PatchMapping("/{bookingId}")
    public BookingDto setBookingStatus(@RequestHeader("X-Sharer-User-Id") Long userId,
                                       @PathVariable Long bookingId,
                                       @RequestParam(value = "approved") boolean approved) {
        return bookingMapper.bookingToBookingDto(bookingService.setBookingStatus(userId, bookingId, approved));
    }

    @GetMapping
    public List<BookingDto> getAllUserBookingsByState(@RequestHeader("X-Sharer-User-Id") Long userId,
                                                     @RequestParam(value = "state", defaultValue = "ALL")
                                                     String state) {
        return bookingService.getAllUserBookingsByState(userId, state).stream()
                .map(x->bookingMapper.bookingToBookingDto(x))
                .collect(Collectors.toList());
    }

    @GetMapping("/owner")
    public List<BookingDto> getAllOwnerBookingsByState(@RequestHeader("X-Sharer-User-Id") Long userId,
                                                      @RequestParam(value = "state", defaultValue = "ALL") String state) {
        return bookingService.getAllOwnerBookingsByState(userId, state).stream()
                .map(value -> bookingMapper.bookingToBookingDto(value))
                .collect(Collectors.toList());
    }
}
