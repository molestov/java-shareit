package ru.practicum.shareit.booking.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import org.springframework.beans.factory.annotation.Autowired;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingDtoWithEntities;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.error.exception.UnknownIdException;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.storage.ItemStorage;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.storage.UserStorage;

import java.sql.Timestamp;
import java.time.LocalDateTime;

@Mapper(componentModel = "spring")
public abstract class BookingMapper {
    @Autowired
    ItemStorage itemStorage;
    @Autowired
    UserStorage userStorage;

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
        return itemStorage.findById(itemId).orElseThrow(UnknownIdException::new);
    }

    @Named("mapUser")
    protected User mapUser(Long userId) {
        return userStorage.findById(userId).orElseThrow(UnknownIdException::new);
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
