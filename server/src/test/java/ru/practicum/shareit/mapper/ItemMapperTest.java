package ru.practicum.shareit.mapper;

import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.user.model.User;

import java.sql.Timestamp;
import java.time.Instant;
import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class ItemMapperTest {
    private final ItemMapper itemMapper = Mappers.getMapper(ItemMapper.class);

    @Test
    public void mapRequestTest() {
        ItemRequest itemRequest = new ItemRequest();
        itemRequest.setId(1L);
        assertEquals(itemMapper.mapRequest(itemRequest), 1L);
    }

    @Test
    public void mapLongTest() {
        assertEquals(itemMapper.mapLong(null), null);
        assertTrue(itemMapper.mapLong(1L).getId() == 1L);
    }

    @Test
    public void setCommentsTest() {
        Item item = createItem();
        item.setOwner(createUser());
        item.setId(1L);
        item.setComments(new ArrayList<>());
        assertTrue(itemMapper.toItemDtoWithBookings(item).getId() == 1L);
    }

    private Booking createBooking() {
        Booking booking = new Booking();
        booking.setId(1L);
        booking.setStatus(BookingStatus.WAITING);
        booking.setStart(Timestamp.from(Instant.now()));
        booking.setEnd(Timestamp.from(Instant.now().plusSeconds(3600)));
        return booking;
    }

    protected User createUser() {
        User user = new User();
        user.setId(1L);
        user.setName("Example");
        user.setEmail("examle@example.com");
        return user;
    }

    private Item createItem() {
        Item item = new Item();
        item.setId(1L);
        item.setName("Example");
        item.setDescription("Example text");
        item.setAvailable(true);
        return item;
    }
}
