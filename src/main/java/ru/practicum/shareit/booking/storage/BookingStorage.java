package ru.practicum.shareit.booking.storage;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import ru.practicum.shareit.booking.model.Booking;

import java.util.List;
import java.util.Optional;

@Repository
public interface BookingStorage extends JpaRepository<Booking, Long> {
    @Query(value = "select * from bookings where booker_id = ?1 order by start_date DESC", nativeQuery = true)
    List<Booking> getAllBookingsByBookerId(Long bookerId);

    @Query(value = "select * from bookings where booker_id = ?1 and start_date > CURRENT_TIMESTAMP " +
            "order by start_date DESC", nativeQuery = true)
    List<Booking> getAllFutureBookingsForBooker(Long bookerId);

    @Query(value = "select * from bookings where booker_id = ?1 and start_date < CURRENT_TIMESTAMP and end_date > CURRENT_TIMESTAMP " +
            "order by start_date DESC", nativeQuery = true)
    List<Booking> getAllCurrentBookingsForBooker(Long bookerId);

    @Query(value = "select * from bookings where booker_id = ?1 and status = 'WAITING'", nativeQuery = true)
    List<Booking> getAllWaitingBookingsForBooker(Long bookerId);

    @Query(value = "select * from bookings where booker_id = ?1 and status = 'REJECTED' " +
            "order by start_date DESC", nativeQuery = true)
    List<Booking> getAllRejectedBookingsForBooker(Long bookerId);

    @Query(value = "select * from bookings b join items i on b.item_id = i.id and i.owner = ?1 " +
            "order by b.start_date DESC", nativeQuery = true)
    List<Booking> getAllBookingsByOwnerId(Long ownerId);

    @Query(value = "select * from bookings b join items i on b.item_id = i.id and i.owner = ?1 " +
            "where b.status = 'WAITING' or b.status = 'APPROVED' order by b.start_date DESC", nativeQuery = true)
    List<Booking> getAllFutureBookingsForOwner(Long userId);

    @Query(value = "select * from bookings b join items i on b.item_id = i.id and i.owner = ?1 " +
            "where b.start_date < CURRENT_TIMESTAMP and b.end_date > CURRENT_TIMESTAMP order by b.start_date DESC",
            nativeQuery = true)
    List<Booking> getAllCurrentBookingsForOwner(Long userId);

    @Query(value = "select * from bookings b join items i on b.item_id = i.id and i.owner = ?1 " +
            "where b.status = 'WAITING'", nativeQuery = true)
    List<Booking> getAllWaitingBookingsForOwner(Long userId);

    @Query(value = "select * from bookings b join items i on b.item_id = i.id and i.owner = ?1 " +
            "where b.status = 'REJECTED'", nativeQuery = true)
    List<Booking> getAllRejectedBookingsForOwner(Long userId);

    @Query(value = "select * from bookings b where b.item_id = ?1 and status = '1' order by b.end_date ASC limit 2",
            nativeQuery = true)
    List<Booking> getBookingsByItemId(Long itemId);

    @Query(value = "select * from bookings b where b.item_id = ?1 and start_date < current_timestamp " +
            "order by b.start_date DESC limit 1", nativeQuery = true)
    Optional<Booking> getLastBooking(Long itemId);

    @Query(value = "select * from bookings b where b.item_id = ?1 and start_date > current_timestamp and status = 'APPROVED' " +
            "order by b.start_date ASC limit 1", nativeQuery = true)
    Optional<Booking> getNextBooking(Long itemId);

    @Query(value = "select * from bookings b where b.item_id = ?1 and b.booker_id = ?2 limit 1",
            nativeQuery = true)
    Booking getBookingByItemIdAndBookerId(Long itemId, Long bookerId);

    @Query(value = "select * from bookings where booker_id = ?1 and end_date < CURRENT_TIMESTAMP " +
            "order by start_date DESC", nativeQuery = true)
    List<Booking> getAllPastBookingsForBooker(Long userId);

    @Query(value = "select * from bookings b join items i on b.item_id = i.id and i.owner = ?1 " +
            "where b.end_date < CURRENT_TIMESTAMP order by b.start_date DESC", nativeQuery = true)
    List<Booking> getAllPastBookingsForOwner(Long userId);
}
