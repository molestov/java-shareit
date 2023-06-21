package ru.practicum.shareit.item.mapper;

import org.mapstruct.Mapper;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;

@Mapper
public interface ItemMapper {
    ItemDto toItemDto(Item item);

    Item toItem(ItemDto item);

    CommentDto toCommentDto(Comment comment);

    Comment toComment(CommentDto commentDto);
}
