package ru.practicum.shareit.booking.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import ru.practicum.shareit.booking.model.Booking;

import java.util.Optional;

public interface BookingRepository extends JpaRepository<Booking, Long>, JpaSpecificationExecutor<Booking>,
        PagingAndSortingRepository<Booking, Long> {
    Page<Booking> findAll(Specification<Booking> spec, Pageable pageable);

    @Query(value = "select * from bookings where booker_id = ?1 and item_id = ?2 and status = 'APPROVED' " +
            "and start_date < CURRENT_TIMESTAMP limit 1", nativeQuery = true)
    Optional<Booking> checkUserBookings(Long bookerId, Long itemId);
}
