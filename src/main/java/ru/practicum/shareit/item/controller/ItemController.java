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
import ru.practicum.shareit.OffsetBasedPageRequest;
import ru.practicum.shareit.booking.mapper.BookingMapper;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.service.BookingService;
import ru.practicum.shareit.error.exception.WrongStateException;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemDtoWithBookings;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.service.ItemService;
import ru.practicum.shareit.request.service.ItemRequestService;

import javax.validation.Valid;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/items")
public class ItemController {
    private final ItemService itemService;

    private final ItemMapper itemMapper;

    private final BookingService bookingService;

    private final BookingMapper bookingMapper;

    private final ItemRequestService itemRequestService;

    @Autowired
    public ItemController(ItemService itemService, ItemMapper itemMapper,
                          BookingService bookingService, BookingMapper bookingMapper, ItemRequestService itemRequestService) {
        this.itemService = itemService;
        this.itemMapper = itemMapper;
        this.bookingService = bookingService;
        this.bookingMapper = bookingMapper;
        this.itemRequestService = itemRequestService;
    }

    @PostMapping
    public ItemDto addItem(@RequestHeader("X-Sharer-User-Id") Long id,
                           @Valid @RequestBody ItemDto itemDto) {
        Item item = itemMapper.toItem(itemDto);
        if (itemDto.getRequestId() != null) {
            item.setRequest(itemRequestService.getRequest(id, itemDto.getRequestId()));
        }
        return itemMapper.toItemDto(itemService.addItem(id, item));
    }

    @PatchMapping("/{id}")
    public ItemDto updateItem(@RequestHeader("X-Sharer-User-Id") Long ownerId,
                              @PathVariable Long id,
                              @Valid @RequestBody ItemDto item) {
        return itemMapper.toItemDto(itemService.updateItem(ownerId, id, item));
    }

    @GetMapping("/{id}")
    public ItemDtoWithBookings getItem(@RequestHeader("X-Sharer-User-Id") Long userId, @PathVariable Long id) {
        Item item = itemService.getItemById(userId, id);
        ItemDtoWithBookings itemDtoWithBookings = itemMapper.toItemDtoWithBookings(item);
        if (item.isOwnerRequest()) {
            setBookings(itemDtoWithBookings);
        }
        return itemDtoWithBookings;
    }

    @GetMapping
    public List<ItemDtoWithBookings> getItemsByOwner(@RequestHeader("X-Sharer-User-Id") Long id,
                                                 @RequestParam(value = "from", defaultValue = "0") int from,
                                                 @RequestParam(value = "size", defaultValue = "9999") int size) {
        if (from < 0) {
            throw new WrongStateException("From cannot be less then 0");
        }
        if (size < 1) {
            throw new WrongStateException("Size cannot be less then 1");
        }
        List<Item> items =  itemService.getItemsByOwnerId(id, new OffsetBasedPageRequest(from, size));
        return items.stream()
                .map(itemMapper::toItemDtoWithBookings)
                .map(this::setBookings)
                .collect(Collectors.toList());
    }

    @GetMapping(value = "/search")
    public List<ItemDto> searchItemByKeyword(@RequestParam(value = "text") String text,
                                             @RequestParam(value = "from", defaultValue = "0") int from,
                                             @RequestParam(value = "size", defaultValue = "9999") int size) {
        List<Item> items =  itemService.findItemsByKeyword(text, new OffsetBasedPageRequest(from, size));
        return items.stream()
                .map(itemMapper::toItemDto)
                .collect(Collectors.toList());
    }

    @PostMapping(value = "/{itemId}/comment")
    public CommentDto addComment(@RequestHeader("X-Sharer-User-Id") Long id,
                                 @PathVariable Long itemId,
                                 @Valid @RequestBody CommentDto comment) {
        return itemMapper.toCommentDto(itemService.addComment(id, itemId, itemMapper.toComment(comment)));
    }

    private ItemDtoWithBookings setBookings(ItemDtoWithBookings itemDtoWithBookings) {
            Optional<Booking> lastBooking = bookingService.getLastBooking(itemDtoWithBookings.getId());
            Optional<Booking> nextBooking = bookingService.getNextBooking(itemDtoWithBookings.getId());
            if (lastBooking.isPresent()) {
                itemDtoWithBookings.setLastBooking(bookingMapper.toBookingDto(lastBooking.get()));
            }
            if (nextBooking.isPresent()) {
                itemDtoWithBookings.setNextBooking(bookingMapper.toBookingDto(nextBooking.get()));
            }
            return itemDtoWithBookings;
    }
}
