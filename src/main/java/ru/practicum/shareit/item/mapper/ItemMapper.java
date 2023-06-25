package ru.practicum.shareit.item.mapper;

import org.mapstruct.Mapper;
import org.springframework.beans.factory.annotation.Autowired;
import ru.practicum.shareit.booking.mapper.BookingMapper;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.storage.BookingStorage;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemDtoWithBookings;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;

import java.util.Optional;


@Mapper(componentModel = "spring")
public abstract class ItemMapper {
    @Autowired
    BookingStorage bookingStorage;
    @Autowired
    BookingMapper bookingMapper;

    public abstract ItemDto toItemDto(Item item);

    public abstract Item toItem(ItemDto item);

    public ItemDtoWithBookings toItemDtoWithBookings(Item item) {
        ItemDtoWithBookings itemDtoWithBookings = new ItemDtoWithBookings(
                item.getId(),
                item.getName(),
                item.getDescription(),
                item.getAvailable(),
                item.getOwner(),
                item.getRequest(),
                null,
                null,
                item.getComments());
        if (item.isOwnerRequest()) {
            Optional<Booking> lastBooking = bookingStorage.getLastBooking(item.getId());
            Optional<Booking> nextBooking = bookingStorage.getNextBooking(item.getId());
            if (lastBooking.isPresent()) {
                itemDtoWithBookings.setLastBooking(bookingMapper.toBookingDto(lastBooking.get()));
            }
            if (nextBooking.isPresent()) {
                itemDtoWithBookings.setNextBooking(bookingMapper.toBookingDto(nextBooking.get()));
            }
        }
        return itemDtoWithBookings;
    }

    public abstract CommentDto toCommentDto(Comment comment);

    public abstract Comment toComment(CommentDto commentDto);

}
