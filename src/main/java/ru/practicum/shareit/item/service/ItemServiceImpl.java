package ru.practicum.shareit.item.service;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.BeanWrapper;
import org.springframework.beans.BeanWrapperImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.exception.EmptyNameException;
import ru.practicum.shareit.item.exception.IllegalUserException;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.storage.ItemStorage;
import ru.practicum.shareit.user.exception.UnknownIdException;
import ru.practicum.shareit.user.storage.UserStorage;

import java.beans.FeatureDescriptor;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Stream;

@Service
public class ItemServiceImpl implements ItemService {
    ItemStorage itemStorage;
    UserStorage userStorage;
    private Long id = 1L;

    @Autowired
    public ItemServiceImpl(ItemStorage itemStorage, UserStorage userStorage) {
        this.itemStorage = itemStorage;
        this.userStorage = userStorage;
    }

    @Override
    public Item addItem(Long ownerId, Item item) {
        if(!userStorage.checkUserId(ownerId)) {
            throw new UnknownIdException();
        }
        if(item.getName().equals("")) {
            throw new EmptyNameException();
        }
        item.setId(id);
        item.setOwner(ownerId);
        id++;
        return itemStorage.addItem(item);
    }

    @Override
    public Item updateItem(Long ownerId, Long id, ItemDto item) {
        Item itemFromDb = itemStorage.getItemById(id);
        if(!Objects.equals(itemFromDb.getOwner(), ownerId)) {
            throw new IllegalUserException();
        }
        String[] ignoredProperties = getNullPropertyNames(item);
        BeanUtils.copyProperties(item, itemFromDb, ignoredProperties);
        return itemStorage.updateItem(itemFromDb);
    }

    private static String[] getNullPropertyNames(Object object) {
        final BeanWrapper wrappedSource = new BeanWrapperImpl(object);
        return Stream.of(wrappedSource.getPropertyDescriptors())
                .map(FeatureDescriptor::getName)
                .filter(propertyName -> wrappedSource.getPropertyValue(propertyName) == null)
                .toArray(String[]::new);
    }

    @Override
    public Item getItemById(Long id) {
        if (!itemStorage.checkItemId(id)) {
            return null;
        }
        return itemStorage.getItemById(id);
    }

    @Override
    public List<Item> getItemByOwnerId(Long id) {
        return itemStorage.getItemByOwnerId(id);
    }

    @Override
    public List<Item> searchItemByKeyword(String keyword) {
        if(keyword.isEmpty()) {
            return new ArrayList<>();
        }
        return itemStorage.searchItemByKeyword(keyword);
    }
}
