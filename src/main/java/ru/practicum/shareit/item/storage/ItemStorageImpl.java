package ru.practicum.shareit.item.storage;

import org.springframework.stereotype.Component;
import ru.practicum.shareit.item.model.Item;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
public class ItemStorageImpl implements ItemStorage{
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
    public List<Item> getItemByOwnerId(Long id) {
        List<Item> result = new ArrayList<>();
        for (Map.Entry<Long, Item> entry : items.entrySet()) {
            if (entry.getValue().getOwner().equals(id)) {
                result.add(entry.getValue());
            }
        }
        return result;
    }

    @Override
    public List<Item> searchItemByKeyword(String keyword) {
        List<Item> result = new ArrayList<>();
        for (Map.Entry<Long, Item> entry : items.entrySet()) {
            if (entry.getValue().getName().toLowerCase().contains(keyword.toLowerCase())
                    && entry.getValue().getAvailable()) {
                result.add(entry.getValue());
            } else if (entry.getValue().getDescription().toLowerCase().contains(keyword.toLowerCase())
                    && entry.getValue().getAvailable()){
                result.add(entry.getValue());
            }
        }
        return result;
    }

    @Override
    public boolean checkItemId(Long id) {
        if (items.containsKey(id)) {
            return true;
        }
        return false;
    }
}
