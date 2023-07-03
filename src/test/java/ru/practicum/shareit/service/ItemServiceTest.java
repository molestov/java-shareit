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
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.error.exception.DuplicatedEmailException;
import ru.practicum.shareit.error.exception.EmptyNameException;
import ru.practicum.shareit.error.exception.IllegalUserException;
import ru.practicum.shareit.error.exception.UnavailableItemException;
import ru.practicum.shareit.error.exception.UnknownIdException;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Comment;
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

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
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

    @Mock
    BookingRepository bookingRepository;

    private Item item;

    private User user;

    private ItemDto itemDto;

    private Booking booking;

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
    void testCreateItemWithError1() {
        item.setName("");

        EmptyNameException exception = assertThrows(EmptyNameException.class,
                () -> itemService.addItem(1L, item));

        assertEquals("Name field cannot be empty", exception.getMessage());
    }

    @Test
    void testCreateItemWithError2() {
        item.setAvailable(false);
        item.setDescription(null);

        EmptyNameException exception = assertThrows(EmptyNameException.class,
                () -> itemService.addItem(1L, item));

        assertEquals("Name field cannot be empty", exception.getMessage());
    }

    @Test
    void testCreateItemWithError3() {
        when(userRepository.existsById(anyLong()))
                .thenReturn(false);

        UnknownIdException exception = assertThrows(UnknownIdException.class,
                () -> itemService.addItem(1L, item));

        assertEquals("Id not found", exception.getMessage());
    }

    @Test
    void testCreateItemWithError4() {
        when(userRepository.existsById(anyLong()))
                .thenReturn(true);
        when(userRepository.findById(anyLong()))
                .thenReturn(Optional.empty());

        UnknownIdException exception = assertThrows(UnknownIdException.class,
                () -> itemService.addItem(1L, item));

        assertTrue(exception.getClass().equals(UnknownIdException.class));
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
    void testUpdateItemWithError1() {
        item.getOwner().setId(1L);
        when(itemRepository.findById(anyLong()))
                .thenReturn(Optional.empty());

        UnknownIdException exception = assertThrows(UnknownIdException.class,
                () -> itemService.updateItem(1L, 1L, itemDto));

        assertTrue(exception.getClass().equals(UnknownIdException.class));
    }

    @Test
    void testUpdateItemWithError2() {
        item.getOwner().setId(2L);
        when(itemRepository.findById(anyLong()))
                .thenReturn(Optional.of(item));

        IllegalUserException exception = assertThrows(IllegalUserException.class,
                () -> itemService.updateItem(1L, 1L, itemDto));

        assertEquals("Wrong user id provided", exception.getMessage());
    }

    @Test
    void testGetItemById() {
        when(itemRepository.findById(anyLong()))
                .thenReturn(Optional.of(item));

        Item savedItem = itemService.getItemById(1L, 1L);

        Assertions.assertEquals(savedItem.getId(), 1L);
    }

    @Test
    void testGetItemByIdWithError() {
        when(itemRepository.findById(anyLong()))
                .thenReturn(Optional.empty());

        UnknownIdException exception = assertThrows(UnknownIdException.class,
                () -> itemService.getItemById(1L, 1L));

        assertTrue(exception.getClass().equals(UnknownIdException.class));
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

    @Test
    void testAddComment() {
        Comment comment = new Comment();
        comment.setId(1L);
        comment.setText("example");
        comment.setAuthor(createUser());
        when(userRepository.findById(anyLong()))
                .thenReturn(Optional.of(user));
        when(itemRepository.findById(anyLong()))
                .thenReturn(Optional.of(item));
        when(bookingRepository.checkUserBookings(anyLong(), anyLong()))
                .thenReturn(Optional.of(createBooking()));
        when(commentRepository.save(any(Comment.class)))
                .thenReturn(comment);

        Comment savedComment = itemService.addComment(1L, 1L, comment);

        Assertions.assertEquals(savedComment.getId(), 1L);
    }

    @Test
    void testAddCommentWithError1() {
        Comment comment = new Comment();
        comment.setId(1L);
        comment.setText("example");
        comment.setAuthor(createUser());
        when(userRepository.findById(anyLong()))
                .thenReturn(Optional.empty());

        UnknownIdException exception = assertThrows(UnknownIdException.class,
                () -> itemService.addComment(1L, 1L, comment));

        assertTrue(exception.getClass().equals(UnknownIdException.class));
    }

    @Test
    void testAddCommentWithError2() {
        Comment comment = new Comment();
        comment.setId(1L);
        comment.setText("example");
        comment.setAuthor(createUser());
        when(userRepository.findById(anyLong()))
                .thenReturn(Optional.of(user));
        when(itemRepository.findById(anyLong()))
                .thenReturn(Optional.empty());

        UnknownIdException exception = assertThrows(UnknownIdException.class,
                () -> itemService.addComment(1L, 1L, comment));

        assertTrue(exception.getClass().equals(UnknownIdException.class));
    }

    @Test
    void testAddCommentWithError3() {
        Comment comment = new Comment();
        comment.setId(1L);
        comment.setText("example");
        comment.setAuthor(createUser());
        when(userRepository.findById(anyLong()))
                .thenReturn(Optional.of(user));
        when(itemRepository.findById(anyLong()))
                .thenReturn(Optional.of(item));
        when(bookingRepository.checkUserBookings(anyLong(), anyLong()))
                .thenReturn(Optional.empty());

        UnavailableItemException exception = assertThrows(UnavailableItemException.class,
                () -> itemService.addComment(1L, 1L, comment));

        assertEquals("You never book the item", exception.getMessage());
    }

    @Test
    void testAddCommentWithError4() {
        Comment comment = new Comment();
        comment.setId(1L);
        comment.setText("");
        comment.setAuthor(createUser());
        when(userRepository.findById(anyLong()))
                .thenReturn(Optional.of(user));
        when(itemRepository.findById(anyLong()))
                .thenReturn(Optional.of(item));
        when(bookingRepository.checkUserBookings(anyLong(), anyLong()))
                .thenReturn(Optional.of(createBooking()));

        EmptyNameException exception = assertThrows(EmptyNameException.class,
                () -> itemService.addComment(1L, 1L, comment));

        assertEquals("Text field cannot be empty", exception.getMessage());
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
