package ru.practicum.shareit.item.storage;

import org.springframework.stereotype.Component;
import ru.practicum.shareit.item.model.Item;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

@Component
public class ItemStorageImpl implements ItemStorage {
    private Map<Long, Item> items = new HashMap<>();

    @Override
    public Item addItem(Item item) {
        items.put(item.getId(), item);
        return items.get(item.getId());
    }

    @Override
    public Item updateItem(Item item) {
        items.replace(item.getId(), item);
        return items.get(item.getId());
    }

    @Override
    public Item getItemById(Long id) {
        return items.get(id);
    }

    @Override
    public List<Item> getItemsByOwnerId(Long id) {
        return items.values().stream()
                .filter(Item -> Objects.equals(Item.getOwner(), id))
                .collect(Collectors.toList());
    }

    @Override
    public List<Item> searchItemsByKeyword(String keyword) {
        return items.values().stream()
                .filter(Item -> Item.getName().toLowerCase().contains(keyword.toLowerCase()) && Item.getAvailable()
                        || Item.getDescription().toLowerCase().contains(keyword.toLowerCase()) && Item.getAvailable())
                .collect(Collectors.toList());
    }

    @Override
    public boolean checkItemId(Long id) {
        return items.containsKey(id);
    }
}
