package ru.practicum.shareit.booking.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingDtoWithEntities;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

import java.sql.Timestamp;
import java.time.LocalDateTime;

@Mapper(componentModel = "spring")
public abstract class BookingMapper {

    @Mapping(source = "start", target = "start", qualifiedByName = "mapLdt")
    @Mapping(source = "end", target = "end", qualifiedByName = "mapLdt")
    public abstract BookingDtoWithEntities bookingToBookingDtoWithEntities(Booking booking);

    @Mapping(source = "itemId", target = "item", qualifiedByName = "mapItem")
    @Mapping(source = "bookerId", target = "booker", qualifiedByName = "mapUser")
    public abstract Booking bookingDtoToBooking(BookingDto bookingDto);

    @Mapping(source = "booker", target = "bookerId", qualifiedByName = "mapLong")
    public abstract BookingDto toBookingDto(Booking booking);

    @Named("mapItem")
    protected Item mapItem(Long itemId) {
        Item item = new Item();
        item.setId(itemId);
        return item;
    }

    @Named("mapUser")
    protected User mapUser(Long userId) {
        User user = new User();
        user.setId(userId);
        return user;
    }

    @Named("mapLdt")
    protected LocalDateTime mapLdt(Timestamp value) {
        return value.toLocalDateTime();
    }

    @Named("mapLong")
    protected Long mapLong(User user) {
        return user.getId();
    }

    protected Timestamp mapTimestamp(LocalDateTime value) {
        return Timestamp.valueOf(value);
    }
}
