package ru.practicum.shareit.item.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
public class ItemDto {
    private Long id;
    private String name;
    private String description;
    private Boolean available;
    private Long owner;
    private Long requestId;
    private List<CommentDto> comments = new ArrayList<>();
}
