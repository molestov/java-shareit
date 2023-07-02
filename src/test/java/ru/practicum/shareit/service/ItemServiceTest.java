package ru.practicum.shareit.service;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Pageable;
import ru.practicum.shareit.OffsetBasedPageRequest;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.CommentRepository;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.item.service.ItemServiceImpl;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.sql.Timestamp;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class ItemServiceTest {
    @InjectMocks
    ItemServiceImpl itemService;

    @Mock
    ItemRepository itemRepository;

    @Mock
    UserRepository userRepository;

    @Mock
    CommentRepository commentRepository;

    private Item item;

    private User user;

    private ItemDto itemDto;

    @BeforeEach
    public void setup() {
        item = createItem();
        user = createUser();
        item.setOwner(user);
        item.getOwner().setId(2L);
        itemDto = new ItemDto();
        itemDto.setId(1L);
    }

    @Test
    void testCreateItem() {
        when(userRepository.existsById(anyLong()))
                .thenReturn(true);
        when(userRepository.findById(anyLong()))
                .thenReturn(Optional.of(user));
        when(itemRepository.save(any(Item.class)))
                .thenReturn(item);

        Item savedItem = itemService.addItem(1L, item);

        Assertions.assertEquals(savedItem.getId(), 1L);
    }

    @Test
    void testUpdateItem() {
        item.getOwner().setId(1L);
        when(itemRepository.findById(anyLong()))
                .thenReturn(Optional.of(item));
        when(itemRepository.save(any(Item.class)))
                .thenReturn(item);

        Item savedItem = itemService.updateItem(1L, 1L, itemDto);

        Assertions.assertEquals(savedItem.getId(), 1L);
    }

    @Test
    void testGetItemById() {
        when(itemRepository.findById(anyLong()))
                .thenReturn(Optional.of(item));

        Item savedItem = itemService.getItemById(1L, 1L);

        Assertions.assertEquals(savedItem.getId(), 1L);
    }

    @Test
    void testGetItemsByOwnerId() {
        when(itemRepository.findAllByOwnerIdOrderById(anyLong(), any(Pageable.class)))
                .thenReturn(new ArrayList<Item>());

        List<Item> savedItem = itemService.getItemsByOwnerId(1L, new OffsetBasedPageRequest(0, 9999));

        Assertions.assertNotNull(savedItem);
    }

    @Test
    void testGetItemsByKeyword() {
        when(itemRepository.findItemsByKeyword(anyString(), any(Pageable.class)))
                .thenReturn(new ArrayList<Item>());

        List<Item> savedItem = itemService.findItemsByKeyword("example", new OffsetBasedPageRequest(0, 9999));

        Assertions.assertNotNull(savedItem);
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
