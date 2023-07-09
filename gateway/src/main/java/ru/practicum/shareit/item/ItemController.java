package ru.practicum.shareit.item;

import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;

import javax.validation.Valid;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;

@RestController
@RequestMapping("/items")
@AllArgsConstructor
@Validated
public class ItemController {
    ItemClient itemClient;

    @PostMapping
    public ResponseEntity<Object> addItem(@RequestHeader("X-Sharer-User-Id") Long id,
                                          @Valid @RequestBody ItemDto itemDto) {
        return itemClient.addItem(id, itemDto);
    }

    @PatchMapping("/{id}")
    public ResponseEntity<Object> updateItem(@RequestHeader("X-Sharer-User-Id") Long ownerId,
                                             @PathVariable Long id,
                                             @Valid @RequestBody ItemDto item) {
        return itemClient.updateItem(ownerId, id, item);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Object> getItem(@RequestHeader("X-Sharer-User-Id") Long userId, @PathVariable Long id) {
        return itemClient.getItem(userId, id);
    }

    @GetMapping
    public ResponseEntity<Object> getItemsByOwner(@RequestHeader("X-Sharer-User-Id") Long id,
                                                     @RequestParam(value = "from", defaultValue = "0")
                                                     @PositiveOrZero int from,
                                                     @RequestParam(value = "size", defaultValue = "9999")
                                                     @Positive int size) {
        return itemClient.getItemsForOwner(id, from, size);
    }

    @GetMapping(value = "/search")
    public ResponseEntity<Object> searchItemByKeyword(@RequestHeader("X-Sharer-User-Id") Long id,
                                             @RequestParam(value = "text") String text,
                                             @RequestParam(value = "from", defaultValue = "0")
                                             @PositiveOrZero int from,
                                             @RequestParam(value = "size", defaultValue = "9999")
                                             @Positive int size) {
        return itemClient.searchItemByKeyword(id, text, from, size);
    }

    @PostMapping(value = "/{itemId}/comment")
    public ResponseEntity<Object> addComment(@RequestHeader("X-Sharer-User-Id") Long id,
                                 @PathVariable Long itemId,
                                 @Valid @RequestBody CommentDto comment) {
        return itemClient.addComment(id, itemId, comment);
    }
}
