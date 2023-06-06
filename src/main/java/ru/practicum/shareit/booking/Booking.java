package ru.practicum.shareit.booking;

import lombok.Data;

import java.sql.Timestamp;

@Data
public class Booking {
    private Long id;
    private Timestamp start;
    private Timestamp end;
    private Long item;
    private Long booker;
    private BookingStatus status;
}
