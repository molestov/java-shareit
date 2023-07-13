package ru.practicum.shareit.item;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;

import javax.validation.Valid;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;

@Controller
@RequestMapping("/items")
@AllArgsConstructor
@Slf4j
@Validated
public class ItemController {
    private final ItemClient itemClient;

    @PostMapping
    public ResponseEntity<Object> addItem(@RequestHeader("X-Sharer-User-Id") Long id,
                                          @Valid @RequestBody ItemDto itemDto) {
        log.info("Create new item: {}", itemDto);
        return itemClient.addItem(id, itemDto);
    }

    @PatchMapping("/{id}")
    public ResponseEntity<Object> updateItem(@RequestHeader("X-Sharer-User-Id") Long ownerId,
                                             @PathVariable Long id,
                                             @Valid @RequestBody ItemDto item) {
        log.info("Update item: {}, {}", id, item);
        return itemClient.updateItem(ownerId, id, item);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Object> getItem(@RequestHeader("X-Sharer-User-Id") Long userId, @PathVariable Long id) {
        log.info("Get item: {}", id);
        return itemClient.getItem(userId, id);
    }

    @GetMapping
    public ResponseEntity<Object> getItemsByOwner(@RequestHeader("X-Sharer-User-Id") Long id,
                                                     @RequestParam(value = "from", defaultValue = "0")
                                                     @PositiveOrZero int from,
                                                     @RequestParam(value = "size", defaultValue = "20")
                                                     @Positive int size) {
        log.info("Get items ny owner: {}", id);
        return itemClient.getItemsForOwner(id, from, size);
    }

    @GetMapping(value = "/search")
    public ResponseEntity<Object> searchItemByKeyword(@RequestHeader("X-Sharer-User-Id") Long id,
                                             @RequestParam(value = "text") String text,
                                             @RequestParam(value = "from", defaultValue = "0")
                                             @PositiveOrZero int from,
                                             @RequestParam(value = "size", defaultValue = "20")
                                             @Positive int size) {
        log.info("Search by: {}", text);
        return itemClient.searchItemByKeyword(id, text, from, size);
    }

    @PostMapping(value = "/{itemId}/comment")
    public ResponseEntity<Object> addComment(@RequestHeader("X-Sharer-User-Id") Long id,
                                 @PathVariable Long itemId,
                                 @Valid @RequestBody CommentDto comment) {
        log.info("Add comment: {}, {}", itemId, comment);
        return itemClient.addComment(id, itemId, comment);
    }
}
