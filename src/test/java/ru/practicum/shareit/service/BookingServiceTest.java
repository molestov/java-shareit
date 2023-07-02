package ru.practicum.shareit.service;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import ru.practicum.shareit.OffsetBasedPageRequest;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.booking.service.BookingService;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.sql.Timestamp;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class BookingServiceTest {
    @InjectMocks
    BookingService bookingService;

    @Mock
    BookingRepository bookingRepository;

    @Mock
    ItemRepository itemRepository;

    @Mock
    UserRepository userRepository;

    private Booking booking;

    private User user;

    private Item item;

    @BeforeEach
    public void setup(){
        booking = createBooking();
        user = createUser();
        item = createItem();
        item.setOwner(user);
        item.getOwner().setId(2L);
        booking.setItem(item);
        booking.setBooker(user);
    }

    @Test
    void testAddBooking() {
        when(itemRepository.findById(anyLong()))
                .thenReturn(Optional.of(item));
        when(bookingRepository.save(any(Booking.class)))
                .thenReturn(booking);

        Booking savedBooking = bookingService.addBooking(1L, booking);

        Assertions.assertEquals(booking.getId(), 1L);
    }

    @Test
    void testGetBooking() {
        item.getOwner().setId(1L);
        when(bookingRepository.findById(anyLong()))
                .thenReturn(Optional.of(booking));

        Booking savedBooking = bookingService.getBooking(1L, 1L);

        Assertions.assertEquals(booking.getId(), 1L);
    }

    @Test
    void testSetBookingStatus() {
        item.getOwner().setId(1L);
        when(bookingRepository.findById(anyLong()))
                .thenReturn(Optional.of(booking));
        when(itemRepository.findById(anyLong()))
                .thenReturn(Optional.of(item));
        when(userRepository.existsById(anyLong()))
                .thenReturn(true);
        when(bookingRepository.save(any(Booking.class)))
                .thenReturn(booking);

        Booking savedBooking = bookingService.setBookingStatus(1L, 1L, true);

        Assertions.assertEquals(booking.getId(), 1L);
    }

    @Test
    void testAllUserBookingsByState() {
        when(userRepository.existsById(anyLong()))
                .thenReturn(true);
        when(bookingRepository.findAll(any(Specification.class), any(Pageable.class)))
                .thenReturn(new PageImpl(new ArrayList<Booking>()));

        List<Booking> savedBookings1 = bookingService.getAllUserBookingsByState(1L, "ALL", new OffsetBasedPageRequest(0, 9999));
        List<Booking> savedBookings2 = bookingService.getAllUserBookingsByState(1L, "CURRENT", new OffsetBasedPageRequest(0, 9999));
        List<Booking> savedBookings3 = bookingService.getAllUserBookingsByState(1L, "PAST", new OffsetBasedPageRequest(0, 9999));
        List<Booking> savedBookings4 = bookingService.getAllUserBookingsByState(1L, "FUTURE", new OffsetBasedPageRequest(0, 9999));
        List<Booking> savedBookings5 = bookingService.getAllUserBookingsByState(1L, "WAITING", new OffsetBasedPageRequest(0, 9999));
        List<Booking> savedBookings6 = bookingService.getAllUserBookingsByState(1L, "REJECTED", new OffsetBasedPageRequest(0, 9999));

        Assertions.assertNotNull(savedBookings1);
        Assertions.assertNotNull(savedBookings2);
        Assertions.assertNotNull(savedBookings3);
        Assertions.assertNotNull(savedBookings4);
        Assertions.assertNotNull(savedBookings5);
        Assertions.assertNotNull(savedBookings6);
    }

    @Test
    void testAllOwnerBookingsByState() {
        when(userRepository.existsById(anyLong()))
                .thenReturn(true);
        when(bookingRepository.findAll(any(Specification.class), any(Pageable.class)))
                .thenReturn(new PageImpl(new ArrayList<Booking>()));

        List<Booking> savedBookings1 = bookingService.getAllUserBookingsByState(1L, "ALL", new OffsetBasedPageRequest(0, 9999));
        List<Booking> savedBookings2 = bookingService.getAllUserBookingsByState(1L, "CURRENT", new OffsetBasedPageRequest(0, 9999));
        List<Booking> savedBookings3 = bookingService.getAllUserBookingsByState(1L, "PAST", new OffsetBasedPageRequest(0, 9999));
        List<Booking> savedBookings4 = bookingService.getAllUserBookingsByState(1L, "FUTURE", new OffsetBasedPageRequest(0, 9999));
        List<Booking> savedBookings5 = bookingService.getAllUserBookingsByState(1L, "WAITING", new OffsetBasedPageRequest(0, 9999));
        List<Booking> savedBookings6 = bookingService.getAllUserBookingsByState(1L, "REJECTED", new OffsetBasedPageRequest(0, 9999));

        Assertions.assertNotNull(savedBookings1);
        Assertions.assertNotNull(savedBookings2);
        Assertions.assertNotNull(savedBookings3);
        Assertions.assertNotNull(savedBookings4);
        Assertions.assertNotNull(savedBookings5);
        Assertions.assertNotNull(savedBookings6);
    }

    @Test
    void testGetLastBooking() {
        List<Booking> result = new ArrayList<>();
        result.add(booking);
        when(bookingRepository.findAll(any(Specification.class)))
                .thenReturn(result);

        Optional<Booking> savedBooking = bookingService.getLastBooking(1L);

        Assertions.assertEquals(booking.getId(), 1L);
    }

    @Test
    void testGetNextBooking() {
        List<Booking> result = new ArrayList<>();
        result.add(booking);
        when(bookingRepository.findAll(any(Specification.class)))
                .thenReturn(result);

        Optional<Booking> savedBooking = bookingService.getNextBooking(1L);

        Assertions.assertEquals(booking.getId(), 1L);
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
