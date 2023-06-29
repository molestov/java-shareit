package ru.practicum.shareit.item.service;

import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;

import java.util.List;

public interface ItemService {
    Item addItem(Long ownerId, Item item);

    Item updateItem(Long ownerId, Long id, ItemDto item);

    Item getItemById(Long userId, Long itemId);

    List<Item> getItemsByOwnerId(Long id);

    List<Item> findItemsByKeyword(String keyword);

    Comment addComment(Long id, Long itemId, Comment comment);
}
