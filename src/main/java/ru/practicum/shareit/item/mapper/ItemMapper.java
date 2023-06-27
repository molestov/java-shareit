package ru.practicum.shareit.item.mapper;

import org.mapstruct.Mapper;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemDtoWithBookings;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.user.model.User;

import java.util.ArrayList;
import java.util.stream.Collectors;

@Mapper(componentModel = "spring")
public abstract class ItemMapper {

    public abstract ItemDto toItemDto(Item item);

    public Long map(ItemRequest value) {
        if (value == null) {
            return null;
        }
        return value.getId();
    }

    public ItemRequest mapLong(Long value) {
        if (value == null) {
            return null;
        }
        ItemRequest itemRequest = new ItemRequest();
        itemRequest.setId(value);
        return itemRequest;
    }

    public abstract Item toItem(ItemDto item);

    public ItemDtoWithBookings toItemDtoWithBookings(Item item) {
        ItemDtoWithBookings itemDtoWithBookings = new ItemDtoWithBookings(
                item.getId(),
                item.getName(),
                item.getDescription(),
                item.getAvailable(),
                item.getOwner().getId(),
                null,
                null,
                null,
                new ArrayList<>());
        if (item.getComments() != null) {
            itemDtoWithBookings.setComments(item.getComments().stream()
                    .map(this::toCommentDto)
                    .collect(Collectors.toList()));
        }
        return itemDtoWithBookings;
    }

    public abstract CommentDto toCommentDto(Comment comment);

    public Long map(Item value) {
        return value.getId();
    }

    public Long map(User value) {
        return value.getId();
    }

    public abstract Comment toComment(CommentDto commentDto);

    public Item map(Long value) {
        Item item = new Item();
        item.setId(value);
        return item;
    }

    public User mapUser(Long value) {
        User user = new User();
        user.setId(value);
        return user;
    }

}
