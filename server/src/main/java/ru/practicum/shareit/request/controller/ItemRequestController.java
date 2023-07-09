package ru.practicum.shareit.request.controller;

import lombok.AllArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.item.service.ItemService;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.mapper.ItemRequestMapper;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.request.service.ItemRequestService;

import java.util.List;

@RestController
@AllArgsConstructor
@RequestMapping(path = "/requests")
public class ItemRequestController {
    private final ItemRequestService itemRequestService;
    private final ItemRequestMapper itemRequestMapper;
    private final ItemService itemService;
    private ItemMapper itemMapper;

    @PostMapping
    public ItemRequestDto addItemRequest(@RequestHeader("X-Sharer-User-Id") Long id,
                                         @RequestBody ItemRequestDto itemRequestDto) {
        ItemRequest itemRequest = itemRequestMapper.toItemRequest(itemRequestDto);
        return itemRequestMapper.toItemRequestDto(itemRequestService.addItemRequest(id, itemRequest));
    }

    @GetMapping
    public List<ItemRequestDto> getAllRequests(@RequestHeader("X-Sharer-User-Id") Long id,
                                               @RequestParam(value = "from") int from,
                                               @RequestParam(value = "size") int size) {
        List<ItemRequestDto> itemRequestDtos = itemRequestMapper.toListDto(itemRequestService.getAllRequests(id,
                PageRequest.of(from / size, size)));
        return setItemDtoToList(itemRequestDtos);
    }

    @GetMapping("/all")
    public List<ItemRequestDto> getAllRequestsWithParameters(@RequestHeader("X-Sharer-User-Id") Long id,
                                                             @RequestParam(value = "from") int from,
                                                             @RequestParam(value = "size") int size) {
        List<ItemRequestDto> itemRequestDtos = itemRequestMapper
                .toListDto(itemRequestService.getAllRequestsWithPages(id, from, size));
        return setItemDtoToList(itemRequestDtos);
    }

    @GetMapping("/{requestId}")
    public ItemRequestDto getRequest(@RequestHeader("X-Sharer-User-Id") Long id, @PathVariable Long requestId) {
        ItemRequestDto itemRequestDto = itemRequestMapper.toItemRequestDto(itemRequestService.getRequest(id, requestId));
        List<ItemDto> items = itemMapper.toListDto(itemService.getItemsByRequest(requestId,
                PageRequest.of(0 / 9999, 9999)));
        itemRequestDto.setItems(items);
        return itemRequestDto;
    }

    private List<ItemRequestDto> setItemDtoToList(List<ItemRequestDto> itemRequestDtos) {
        itemRequestDtos
                .forEach(request ->
                        request.setItems(itemMapper.toListDto(itemService.getItemsByRequest(request.getId(),
                                PageRequest.of(0 / 9999, 9999)))));
        return itemRequestDtos;
    }
}
