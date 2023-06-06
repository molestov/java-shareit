package ru.practicum.shareit.item.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.service.ItemService;

import javax.validation.Valid;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/items")
public class ItemController {
    private ItemService itemService;

    private ItemMapper itemMapper;

    @Autowired
    public ItemController(ItemService itemService, ItemMapper itemMapper) {
        this.itemService = itemService;
        this.itemMapper = itemMapper;
    }

    @PostMapping
    public ItemDto addItem(@RequestHeader("X-Sharer-User-Id") Long id,
                           @Valid @RequestBody ItemDto item) {
        Item itemEntity = itemMapper.toItem(item);
        return itemMapper.toItemDto(itemService.addItem(id, itemEntity));
    }

    @PatchMapping("/{id}")
    public ItemDto updateItem(@RequestHeader("X-Sharer-User-Id") Long ownerId,
                              @PathVariable Long id,
                              @Valid @RequestBody ItemDto item) {
        return itemMapper.toItemDto(itemService.updateItem(ownerId, id, item));
    }

    @GetMapping("/{id}")
    public ItemDto getItem(@PathVariable Long id) {
        return itemMapper.toItemDto(itemService.getItemById(id));
    }

    @GetMapping
    public List<ItemDto> getItemsByOwner(@RequestHeader("X-Sharer-User-Id") Long id) {
        List<Item> items =  itemService.getItemByOwnerId(id);
        return items.stream()
                .map(itemMapper::toItemDto)
                .collect(Collectors.toList());
    }

    @GetMapping(value = "/search")
    public List<ItemDto> searchItemByKeyword(@RequestParam(value = "text") String text) {
        List<Item> items =  itemService.searchItemByKeyword(text);
        return items.stream()
                .map(itemMapper::toItemDto)
                .collect(Collectors.toList());
    }
}
