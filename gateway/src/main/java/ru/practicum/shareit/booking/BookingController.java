package ru.practicum.shareit.booking;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import ru.practicum.shareit.booking.dto.BookItemRequestDto;
import ru.practicum.shareit.booking.dto.BookingState;
import ru.practicum.shareit.exception.WrongStateException;

import javax.validation.Valid;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;

@Controller
@AllArgsConstructor
@RequestMapping(path = "/bookings")
@Slf4j
@Validated
public class BookingController {
	private final BookingClient bookingClient;

	@PostMapping
	public ResponseEntity<Object> addBooking(@RequestHeader("X-Sharer-User-Id") Long id,
											 @Valid @RequestBody BookItemRequestDto bookingDto) {
		log.info("Create new booking: {}", bookingDto);
		return bookingClient.bookItem(id, bookingDto);
	}

	@GetMapping("/{bookingId}")
	public ResponseEntity<Object> getBooking(@RequestHeader("X-Sharer-User-Id") Long userId,
											 @PathVariable Long bookingId) {
		log.info("Get booking: {}", bookingId);
		return bookingClient.getBooking(userId, bookingId);
	}

	@PatchMapping("/{bookingId}")
	public ResponseEntity<Object> setBookingStatus(@RequestHeader("X-Sharer-User-Id") Long userId,
												   @PathVariable Long bookingId,
												   @RequestParam(value = "approved") boolean approved) {
		log.info("Set booking status: {}, {}", bookingId, approved);
		return bookingClient.setBookingStatus(userId, bookingId, approved);
	}

	@GetMapping
	public ResponseEntity<Object> getAllUserBookingsByState(@RequestHeader("X-Sharer-User-Id") Long userId,
												@RequestParam(value = "state", defaultValue = "ALL") String state,
												@RequestParam(value = "from", defaultValue = "0")
												@PositiveOrZero int from,
												@RequestParam(value = "size", defaultValue = "20")
												@Positive int size) {
		log.info("Get all bookings by state: {}", state);
		BookingState bookingState = BookingState.from(state).orElseThrow(() ->
				new WrongStateException("Unknown state: " + state));

		return bookingClient.getBookings(userId, bookingState, from, size);
	}

	@GetMapping("/owner")
	public ResponseEntity getAllOwnerBookingsByState(@RequestHeader("X-Sharer-User-Id") Long userId,
												@RequestParam(value = "state", defaultValue = "ALL") String state,
												@RequestParam(value = "from", defaultValue = "0")
												@PositiveOrZero int from,
												@RequestParam(value = "size", defaultValue = "20")
												@Positive int size) {
		log.info("Owner request. Get all bookings by state : {}", state);
		BookingState bookingState = BookingState.from(state).orElseThrow(() ->
				new WrongStateException("Unknown state: " + state));
		return bookingClient.getBookingsForOwner(userId, bookingState, from, size);
	}
}
