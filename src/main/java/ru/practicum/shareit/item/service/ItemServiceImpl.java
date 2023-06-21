package ru.practicum.shareit.item.service;

import lombok.Data;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.BeanWrapper;
import org.springframework.beans.BeanWrapperImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.booking.storage.BookingStorage;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.exception.EmptyNameException;
import ru.practicum.shareit.item.exception.IllegalUserException;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.storage.CommentStorage;
import ru.practicum.shareit.item.storage.ItemStorage;
import ru.practicum.shareit.user.exception.UnknownIdException;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.storage.UserStorage;

import java.beans.FeatureDescriptor;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
@Data
public class ItemServiceImpl implements ItemService {
    @Autowired
    private final ItemStorage itemStorage;
    @Autowired
    private final UserStorage userStorage;
    @Autowired
    private final BookingStorage bookingStorage;
    @Autowired
    private final CommentStorage commentStorage;

    @Override
    public Item addItem(Long ownerId, Item item) {
        if (item.getName().equals("")) {
            throw new EmptyNameException();
        }
        if (!userStorage.existsById(ownerId)) {
            throw new UnknownIdException();
        }
        item.setOwner(ownerId);
        return itemStorage.save(item);
    }

    @Override
    public Item updateItem(Long ownerId, Long id, ItemDto item) {
        Item itemFromDb = itemStorage.findById(id).get();
        if (!Objects.equals(itemFromDb.getOwner(), ownerId)) {
            throw new IllegalUserException();
        }
        String[] ignoredProperties = getNullPropertyNames(item);
        BeanUtils.copyProperties(item, itemFromDb, ignoredProperties);
        return itemStorage.save(itemFromDb);
    }

    private static String[] getNullPropertyNames(Object object) {
        final BeanWrapper wrappedSource = new BeanWrapperImpl(object);
        return Stream.of(wrappedSource.getPropertyDescriptors())
                .map(FeatureDescriptor::getName)
                .filter(propertyName -> wrappedSource.getPropertyValue(propertyName) == null)
                .toArray(String[]::new);
    }

    @Override
    public Item getItemById(Long userId, Long itemId) {
        Item item = itemStorage.findById(itemId).orElseThrow(UnknownIdException::new);
        if (Objects.equals(item.getOwner(), userId)) {
            return setComments(setBookings(item));
        }
        return setComments(item);
    }

    @Override
    public List<Item> getItemsByOwnerId(Long id) {
        List<Item> items = itemStorage.findAllByOwnerOrderById(id);
        return items.stream()
                .map(this::setBookings)
                .collect(Collectors.toList());
    }

    @Override
    public List<Item> findItemsByKeyword(String keyword) {
        if (keyword.isEmpty()) {
            return new ArrayList<>();
        }
        return itemStorage.findItemsByKeyword(keyword);
    }

    @Override
    public Comment addComment(Long id, Long itemId, Comment comment) {
        if (!checkUserBookings(id, itemId)) {
            throw new EmptyNameException();
        }
        if (comment.getText().isEmpty()) {
            throw new EmptyNameException();
        }
        return setAuthorName(commentStorage.save(comment));
    }

    public void setItemAvailableFalse(Long itemId) {
        Item item = itemStorage.findById(itemId).orElseThrow(UnknownIdException::new);
        item.setAvailable(false);
        itemStorage.save(item);
    }

    private Item setBookings(Item item) {
        Optional<Booking> lastBooking = bookingStorage.getLastBooking(item.getId());
        Optional<Booking> nextBooking = bookingStorage.getNextBooking(item.getId());
        if (lastBooking.isPresent()) {
            item.setLastBooking(lastBooking.get());
        }
        if (nextBooking.isPresent()) {
            item.setNextBooking(nextBooking.get());
        }
        return item;
    }

    boolean checkUserBookings(Long userId, Long itemId) {
        List<Booking> bookings = bookingStorage.getAllBookingsByBookerId(userId);
        for (Booking booking : bookings) {
            if (Objects.equals(booking.getItemId(), itemId) && booking.getStatus() == BookingStatus.APPROVED
                    && booking.getStart().before(Timestamp.valueOf(LocalDateTime.now()))) {
                return true;
            }
        }
        return false;
    }

    private Comment setAuthorName(Comment comment) {
        User author = userStorage.findById(comment.getAuthor()).get();
        comment.setAuthorName(author.getName());
        return comment;
    }

    private Item setComments(Item item) {
        List<Comment> comments = commentStorage.findCommentsByItem(item.getId()).stream()
                .map(this::setAuthorName)
                .collect(Collectors.toList());
        item.setComments(comments);
        return item;
    }
}
