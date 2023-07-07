package ru.practicum.shareit.item.service;

import lombok.AllArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.BeanWrapper;
import org.springframework.beans.BeanWrapperImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.error.exception.EmptyNameException;
import ru.practicum.shareit.error.exception.IllegalUserException;
import ru.practicum.shareit.error.exception.UnavailableItemException;
import ru.practicum.shareit.error.exception.UnknownIdException;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.CommentRepository;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.request.service.ItemRequestService;
import ru.practicum.shareit.user.repository.UserRepository;

import java.beans.FeatureDescriptor;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
@AllArgsConstructor
public class ItemServiceImpl implements ItemService {
    @Autowired
    private final ItemRepository itemRepository;

    @Autowired
    private final UserRepository userRepository;

    @Autowired
    private final BookingRepository bookingRepository;

    @Autowired
    private final CommentRepository commentRepository;

    @Autowired
    private final ItemRequestService itemRequestService;

    @Override
    public Item addItem(Long ownerId, Item item) {
        if (item.getAvailable() == null || item.getDescription() == null) {
            throw new EmptyNameException("Name field cannot be empty");
        }
        if (item.getName().equals("")) {
            throw new EmptyNameException("Name field cannot be empty");
        }
        if (!userRepository.existsById(ownerId)) {
            throw new UnknownIdException("Id not found");
        }
        item.setOwner(userRepository.findById(ownerId).orElseThrow(UnknownIdException::new));
        return itemRepository.save(item);
    }

    @Override
    public Item updateItem(Long ownerId, Long id, ItemDto item) {
        Item itemFromDb = itemRepository.findById(id).orElseThrow(UnknownIdException::new);
        if (!Objects.equals(itemFromDb.getOwner().getId(), ownerId)) {
            throw new IllegalUserException("Wrong user id provided");
        }
        String[] ignoredProperties = getNullPropertyNames(item);
        BeanUtils.copyProperties(item, itemFromDb, ignoredProperties);
        return itemRepository.save(itemFromDb);
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
        Item item = itemRepository.findById(itemId).orElseThrow(UnknownIdException::new);
        if (Objects.equals(item.getOwner().getId(), userId)) {
            item.setOwnerRequest(true);
        }
        return setComments(item);
    }

    @Override
    public List<Item> getItemsByOwnerId(Long id, Pageable pageable) {
        List<Item> items = itemRepository.findAllByOwnerIdOrderById(id, pageable);
        items.forEach(item -> item.setOwnerRequest(true));
        return items;
    }

    @Override
    public List<Item> findItemsByKeyword(String keyword, Pageable pageable) {
        if (keyword.isEmpty()) {
            return new ArrayList<>();
        }
        return itemRepository.findItemsByKeyword(keyword, pageable);
    }

    @Override
    public Comment addComment(Long id, Long itemId, Comment comment) {
        comment.setAuthor(userRepository.findById(id).orElseThrow(UnknownIdException::new));
        comment.setItem(itemRepository.findById(itemId).orElseThrow(UnknownIdException::new));
        comment.setCreated(Timestamp.valueOf(LocalDateTime.now()));
        if (!checkUserBookings(id, itemId)) {
            throw new UnavailableItemException("You never book the item");
        }
        if (comment.getText().isEmpty()) {
            throw new EmptyNameException("Text field cannot be empty");
        }
        return setAuthorName(commentRepository.save(comment));
    }

    private boolean checkUserBookings(Long userId, Long itemId) {
        return bookingRepository.checkUserBookings(userId, itemId).isPresent();
    }

    private Comment setAuthorName(Comment comment) {
        comment.setAuthorName(comment.getAuthor().getName());
        return comment;
    }

    private Item setComments(Item item) {
        List<Comment> comments = commentRepository.findCommentsByItemId(item.getId()).stream()
                .map(this::setAuthorName)
                .collect(Collectors.toList());
        item.setComments(comments);
        return item;
    }

    @Override
    public List<Item> getItemsByRequest(Long id, Pageable pageable) {
        return itemRepository.findAllByRequestId(id, pageable);
    }
}
