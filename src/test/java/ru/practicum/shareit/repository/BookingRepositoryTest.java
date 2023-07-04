package ru.practicum.shareit.repository;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import ru.practicum.shareit.OffsetBasedPageRequest;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

import java.sql.Timestamp;
import java.time.Instant;
import java.util.List;

import static ru.practicum.shareit.booking.repository.BookingSpecification.endAfterNow;
import static ru.practicum.shareit.booking.repository.BookingSpecification.endBeforeNow;
import static ru.practicum.shareit.booking.repository.BookingSpecification.hasBookerId;
import static ru.practicum.shareit.booking.repository.BookingSpecification.hasItemId;
import static ru.practicum.shareit.booking.repository.BookingSpecification.hasOwnerId;
import static ru.practicum.shareit.booking.repository.BookingSpecification.hasStatus;
import static ru.practicum.shareit.booking.repository.BookingSpecification.orderByStartDateAsc;
import static ru.practicum.shareit.booking.repository.BookingSpecification.orderByStartDateDesc;
import static ru.practicum.shareit.booking.repository.BookingSpecification.startAfterNow;
import static ru.practicum.shareit.booking.repository.BookingSpecification.startBeforeNow;

@ExtendWith(SpringExtension.class)
@DataJpaTest
public class BookingRepositoryTest {
    @Autowired
    private BookingRepository bookingRepository;

    private Booking booking;

   @Test
    public void testAddBooking() {
        booking = createBooking();
        booking.setItem(createItem());
        booking.setBooker(createUser());
        Booking result = bookingRepository.save(booking);

        Assertions.assertNotNull(result);
    }

    @Test
    public void testGetBooking() {
        Booking booking = bookingRepository.findById(1L).get();
        Assertions.assertNotNull(booking);
    }

    @Test
    public void testBookingsByUserRequest1() {
        List<Booking> result = bookingRepository.findAll(orderByStartDateDesc(hasBookerId(1L)),
                new OffsetBasedPageRequest(0, 9999)).getContent();

        Assertions.assertNotNull(result);
    }

    @Test
    public void testBookingsByUserRequest2() {
        List<Booking> result = bookingRepository.findAll(orderByStartDateDesc(hasBookerId(1L)).and(startAfterNow()),
                new OffsetBasedPageRequest(0, 9999)).getContent();

        Assertions.assertNotNull(result);
    }

    @Test
    public void testBookingsByUserRequest3() {
        List<Booking> result = bookingRepository.findAll(orderByStartDateDesc(hasBookerId(1L).and(startBeforeNow()
                        .and(endAfterNow()))),
                new OffsetBasedPageRequest(0, 9999)).getContent();

        Assertions.assertNotNull(result);
    }

    @Test
    public void testBookingsByUserRequest4() {
        List<Booking> result = bookingRepository.findAll(orderByStartDateDesc(hasBookerId(1L).and(endBeforeNow())),
                new OffsetBasedPageRequest(0, 9999)).getContent();

        Assertions.assertNotNull(result);
    }

    @Test
    public void testBookingsByUserRequest5() {
        List<Booking> result = bookingRepository.findAll(hasBookerId(1L).and(hasStatus(BookingStatus.WAITING)),
                new OffsetBasedPageRequest(0, 9999)).getContent();

        Assertions.assertNotNull(result);
    }

    @Test
    public void testBookingsByOwnerRequest1() {
        List<Booking> result = bookingRepository.findAll(orderByStartDateDesc(hasOwnerId(1L)),
                new OffsetBasedPageRequest(0, 9999)).getContent();

        Assertions.assertNotNull(result);
    }

    @Test
    public void testBookingsByOwnerRequest2() {
        List<Booking> result = bookingRepository.findAll(orderByStartDateDesc(hasOwnerId(1L)).and(startAfterNow()),
                new OffsetBasedPageRequest(0, 9999)).getContent();

        Assertions.assertNotNull(result);
    }

    @Test
    public void testBookingsByOwnerRequest3() {
        List<Booking> result = bookingRepository.findAll(orderByStartDateDesc(hasOwnerId(1L).and(startBeforeNow()
                        .and(endAfterNow()))), new OffsetBasedPageRequest(0, 9999)).getContent();

        Assertions.assertNotNull(result);
    }

    @Test
    public void testBookingsByOwnerRequest4() {
        List<Booking> result = bookingRepository.findAll(orderByStartDateDesc(hasOwnerId(1L).and(endBeforeNow())),
                new OffsetBasedPageRequest(0, 9999)).getContent();

        Assertions.assertNotNull(result);
    }

    @Test
    public void testBookingsByOwnerRequest5() {
        List<Booking> result = bookingRepository.findAll(hasOwnerId(1L).and(hasStatus(BookingStatus.WAITING)),
                new OffsetBasedPageRequest(0, 9999)).getContent();

        Assertions.assertNotNull(result);
    }

    @Test
    public void testBookingsByOwnerRequest6() {
        List<Booking> result = bookingRepository.findAll(hasOwnerId(1L).and(hasStatus(BookingStatus.REJECTED)),
                new OffsetBasedPageRequest(0, 9999)).getContent();

        Assertions.assertNotNull(result);
    }

    @Test
    public void testBookingsByUserRequest6() {
        List<Booking> result = bookingRepository.findAll(hasBookerId(1L).and(hasBookerId(1L)
                        .and(hasStatus(BookingStatus.REJECTED))), new OffsetBasedPageRequest(0, 9999)).getContent();

        Assertions.assertNotNull(result);
    }

    @Test
    public void testGetLastBooking() {
        List<Booking> result = bookingRepository.findAll(orderByStartDateDesc(hasItemId(1L)
                .and(startBeforeNow())));

        Assertions.assertNotNull(result);
    }

    @Test
    public void testGetNextBooking() {
        List<Booking> result = bookingRepository.findAll(orderByStartDateAsc(hasItemId(1L).and(startAfterNow()
                .and(hasStatus(BookingStatus.APPROVED)))));

        Assertions.assertNotNull(result);
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
