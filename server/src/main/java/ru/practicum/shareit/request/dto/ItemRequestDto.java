package ru.practicum.shareit.request.dto;

import lombok.Getter;
import lombok.Setter;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.user.model.User;

import java.sql.Timestamp;
import java.util.List;

@Getter
@Setter
public class ItemRequestDto {
    private Long id;

    private String description;

    private User requestor;

    private Timestamp created;

    private List<ItemDto> items;
}
