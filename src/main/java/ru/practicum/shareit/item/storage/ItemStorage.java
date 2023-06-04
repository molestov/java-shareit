package ru.practicum.shareit.item.storage;

import ru.practicum.shareit.item.model.Item;

import java.util.List;

public interface ItemStorage {
    Item addItem(Item item);

    Item updateItem(Item item);

    Item getItemById(Long id);

    List<Item> getItemByOwnerId(Long id);

    List<Item> searchItemByKeyword(String keyword);

    boolean checkItemId(Long id);
}
