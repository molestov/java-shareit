package ru.practicum.shareit.booking.model;

import lombok.Data;
import lombok.NoArgsConstructor;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Transient;
import javax.validation.constraints.FutureOrPresent;
import java.sql.Timestamp;

@Data
@NoArgsConstructor
@Entity
@Table(name = "bookings", schema = "public")
public class Booking {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(name = "start_date")
    @FutureOrPresent
    private Timestamp start;
    @Column(name = "end_date")
    private Timestamp end;
    @Column(name = "item_id")
    private Long itemId;
    @Transient
    private Item item;
    @Column(name = "booker_id")
    private Long bookerId;
    @Transient
    private User booker;
    @Enumerated(EnumType.STRING)
    private BookingStatus status;
}
