package ru.practicum.shareit.request;

import lombok.Data;

import java.sql.Timestamp;

@Data
public class ItemRequest {
    private Long id;
    private String description;
    private Long requestor;
    private Timestamp created;
}
