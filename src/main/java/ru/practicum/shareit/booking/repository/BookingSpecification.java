package ru.practicum.shareit.booking.repository;

import org.springframework.data.jpa.domain.Specification;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingStatus;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import java.sql.Timestamp;
import java.time.Instant;

public class BookingSpecification {
    public static Specification<Booking> hasBookerId(final Long userId) {
        return new Specification<Booking>() {
            @Override
            public Predicate toPredicate(Root<Booking> root, CriteriaQuery<?> query, CriteriaBuilder criteriaBuilder) {
                return criteriaBuilder.equal(root.get("booker").get("id"), userId);
            }
        };
    }

    public static Specification<Booking> hasItemId(final Long itemId) {
        return new Specification<Booking>() {
            @Override
            public Predicate toPredicate(Root<Booking> root, CriteriaQuery<?> query, CriteriaBuilder criteriaBuilder) {
                return criteriaBuilder.equal(root.get("item").get("id"), itemId);
            }
        };
    }

    public static Specification<Booking> hasOwnerId(final Long userId) {
        return new Specification<Booking>() {
            @Override
            public Predicate toPredicate(Root<Booking> root, CriteriaQuery<?> query, CriteriaBuilder criteriaBuilder) {
                return criteriaBuilder.equal(root.get("item").get("owner").get("id"), userId);
            }
        };
    }

    public static Specification<Booking> hasStatus(final BookingStatus status) {
        return new Specification<Booking>() {
            @Override
            public Predicate toPredicate(Root<Booking> root, CriteriaQuery<?> query, CriteriaBuilder criteriaBuilder) {
                return criteriaBuilder.equal(root.get("status"), status);
            }
        };
    }

    public static Specification<Booking> startAfterNow() {
        return new Specification<Booking>() {
            @Override
            public Predicate toPredicate(Root<Booking> root, CriteriaQuery<?> query, CriteriaBuilder criteriaBuilder) {
                return criteriaBuilder.greaterThan(root.get("start"), Timestamp.from(Instant.now()));
            }
        };
    }

    public static Specification<Booking> startBeforeNow() {
        return new Specification<Booking>() {
            @Override
            public Predicate toPredicate(Root<Booking> root, CriteriaQuery<?> query, CriteriaBuilder criteriaBuilder) {
                return criteriaBuilder.lessThan(root.get("start"), Timestamp.from(Instant.now()));
            }
        };
    }

    public static Specification<Booking> endAfterNow() {
        return new Specification<Booking>() {
            @Override
            public Predicate toPredicate(Root<Booking> root, CriteriaQuery<?> query, CriteriaBuilder criteriaBuilder) {
                return criteriaBuilder.greaterThan(root.get("end"), Timestamp.from(Instant.now()));
            }
        };
    }

    public static Specification<Booking> endBeforeNow() {
        return new Specification<Booking>() {
            @Override
            public Predicate toPredicate(Root<Booking> root, CriteriaQuery<?> query, CriteriaBuilder criteriaBuilder) {
                return criteriaBuilder.lessThan(root.get("end"), Timestamp.from(Instant.now()));
            }
        };
    }

    public static Specification<Booking> orderByStartDateDesc(Specification<Booking> spec) {
        return (root, query, builder) -> {
            query.orderBy(builder.desc(root.get("start")));
            return spec.toPredicate(root, query, builder);
        };
    }

    public static Specification<Booking> orderByStartDateAsc(Specification<Booking> spec) {
        return (root, query, builder) -> {
            query.orderBy(builder.asc(root.get("start")));
            return spec.toPredicate(root, query, builder);
        };
    }
}
