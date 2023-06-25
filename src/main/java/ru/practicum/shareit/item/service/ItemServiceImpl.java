package ru.practicum.shareit.item.service;

import lombok.AllArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.BeanWrapper;
import org.springframework.beans.BeanWrapperImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.booking.storage.BookingStorage;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.error.exception.EmptyNameException;
import ru.practicum.shareit.error.exception.IllegalUserException;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.storage.CommentStorage;
import ru.practicum.shareit.item.storage.ItemStorage;
import ru.practicum.shareit.error.exception.UnknownIdException;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.storage.UserStorage;

import java.beans.FeatureDescriptor;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
@AllArgsConstructor
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
        if (item.getAvailable() == null || item.getDescription() == null) {
            throw new EmptyNameException();
        }
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
        Item itemFromDb = itemStorage.findById(id).orElseThrow(UnknownIdException::new);
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
            item.setOwnerRequest(true);
        }
        return setComments(item);
    }

    @Override
    public List<Item> getItemsByOwnerId(Long id) {
        List<Item> items = itemStorage.findAllByOwnerOrderById(id);
        items.forEach(x -> x.setOwnerRequest(true));
        return items;
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

    boolean checkUserBookings(Long userId, Long itemId) {
        return bookingStorage.checkUserBookings(userId, itemId).isPresent();
    }

    private Comment setAuthorName(Comment comment) {
        User author = userStorage.findById(comment.getAuthor()).orElseThrow(UnknownIdException::new);
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
