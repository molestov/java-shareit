package ru.practicum.shareit.request;

import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import ru.practicum.shareit.request.dto.ItemRequestDto;

import javax.validation.Valid;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;

@RestController
@AllArgsConstructor
@RequestMapping(path = "/requests")
@Validated
public class ItemRequestController {

    ItemRequestClient itemRequestClient;


    @PostMapping
    public ResponseEntity<Object> addItemRequest(@RequestHeader("X-Sharer-User-Id") Long id,
                                                 @Valid @RequestBody ItemRequestDto itemRequestDto) {
        return itemRequestClient.addItemRequest(id, itemRequestDto);
    }

    @GetMapping
    public ResponseEntity<Object> getAllRequests(@RequestHeader("X-Sharer-User-Id") Long id,
                                                 @RequestParam(value = "from", defaultValue = "0")
                                                 @PositiveOrZero int from,
                                                 @RequestParam(value = "size", defaultValue = "9999")
                                                 @Positive int size) {
        return itemRequestClient.getAllRequests(id, from, size);
    }

    @GetMapping("/all")
    public ResponseEntity<Object> getAllRequestsWithParameters(@RequestHeader("X-Sharer-User-Id") Long id,
                                                             @RequestParam(value = "from", defaultValue = "0")
                                                             @PositiveOrZero int from,
                                                             @RequestParam(value = "size", defaultValue = "9999")
                                                             @Positive int size) {
        return itemRequestClient.getAllRequestsWithParameters(id, from, size);
    }

    @GetMapping("/{requestId}")
    public ResponseEntity<Object> getRequest(@RequestHeader("X-Sharer-User-Id") Long id, @PathVariable Long requestId) {
        return itemRequestClient.getRequest(id, requestId);
    }
}
