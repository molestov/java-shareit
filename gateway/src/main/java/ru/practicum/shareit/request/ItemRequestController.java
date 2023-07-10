package ru.practicum.shareit.request;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import ru.practicum.shareit.request.dto.ItemRequestDto;

import javax.validation.Valid;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;

@Controller
@AllArgsConstructor
@RequestMapping(path = "/requests")
@Slf4j
@Validated
public class ItemRequestController {

    private final ItemRequestClient itemRequestClient;


    @PostMapping
    public ResponseEntity<Object> addItemRequest(@RequestHeader("X-Sharer-User-Id") Long id,
                                                 @Valid @RequestBody ItemRequestDto itemRequestDto) {
        log.info("Create new request: {}", itemRequestDto);
        return itemRequestClient.addItemRequest(id, itemRequestDto);
    }

    @GetMapping
    public ResponseEntity<Object> getAllRequests(@RequestHeader("X-Sharer-User-Id") Long id,
                                                 @RequestParam(value = "from", defaultValue = "0")
                                                 @PositiveOrZero int from,
                                                 @RequestParam(value = "size", defaultValue = "20")
                                                 @Positive int size) {
        log.info("Get all requests by: {}", id);
        return itemRequestClient.getAllRequests(id, from, size);
    }

    @GetMapping("/all")
    public ResponseEntity<Object> getAllRequestsWithParameters(@RequestHeader("X-Sharer-User-Id") Long id,
                                                             @RequestParam(value = "from", defaultValue = "0")
                                                             @PositiveOrZero int from,
                                                             @RequestParam(value = "size", defaultValue = "20")
                                                             @Positive int size) {
        log.info("Get all requests with pages by: {}, {}, {}", id, from, size);
        return itemRequestClient.getAllRequestsWithParameters(id, from, size);
    }

    @GetMapping("/{requestId}")
    public ResponseEntity<Object> getRequest(@RequestHeader("X-Sharer-User-Id") Long id, @PathVariable Long requestId) {
        log.info("Get request: {}", requestId);
        return itemRequestClient.getRequest(id, requestId);
    }
}
