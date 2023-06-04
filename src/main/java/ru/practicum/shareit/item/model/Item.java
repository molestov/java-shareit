package ru.practicum.shareit.item.model;

import lombok.Data;
import lombok.NonNull;
import ru.practicum.shareit.request.ItemRequest;

/**
 * TODO Sprint add-controllers.
 */
@Data
public class Item {
    private Long id;
    @NonNull
    private String name;
    @NonNull
    private String description;
    @NonNull
    private Boolean available;
    private Long owner;
    private ItemRequest request;
}
