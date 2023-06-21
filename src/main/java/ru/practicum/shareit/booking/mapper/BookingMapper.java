package ru.practicum.shareit.booking.mapper;

import org.mapstruct.Mapper;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.model.Booking;

import java.sql.Timestamp;
import java.time.LocalDateTime;

@Mapper
public interface BookingMapper {
    BookingDto bookingToBookingDto(Booking booking);

    Booking bookingDtoToBooking(BookingDto bookingDto);

    default Timestamp map(LocalDateTime value) {
        return Timestamp.valueOf(value);
    }

}
