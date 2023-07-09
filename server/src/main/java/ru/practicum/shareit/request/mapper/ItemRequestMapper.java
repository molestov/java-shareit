package ru.practicum.shareit.request.mapper;

import org.mapstruct.Mapper;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.model.ItemRequest;

import java.util.List;

@Mapper(componentModel = "spring")
public abstract class ItemRequestMapper {
    public abstract ItemRequestDto toItemRequestDto(ItemRequest itemRequest);

    public abstract ItemRequest toItemRequest(ItemRequestDto itemRequestDto);

    public abstract List<ItemRequestDto> toListDto(List<ItemRequest> itemRequests);
}
