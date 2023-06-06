package ru.practicum.shareit.item.storage;

import ru.practicum.shareit.item.model.Item;

import java.util.List;

public interface ItemStorage {
    Item addItem(Item item);

    Item updateItem(Item item);

    Item getItemById(Long id);

    List<Item> getItemsByOwnerId(Long id);

    List<Item> searchItemsByKeyword(String keyword);

    boolean checkItemId(Long id);
}
