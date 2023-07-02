package ru.practicum.shareit.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.json.JsonMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import ru.practicum.shareit.error.exception.UnknownIdException;
import ru.practicum.shareit.item.controller.ItemController;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemDtoWithBookings;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.service.ItemService;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;

import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
public class ItemControllerTest {
    @Mock
    ItemService itemService;

    @Mock
    ItemMapper itemMapper;

    @InjectMocks
    ItemController itemController;

    private final ObjectMapper mapper = JsonMapper.builder()
            .addModule(new JavaTimeModule())
            .build();

    private MockMvc mvc;

    private ItemDto itemDto;

    private ItemDtoWithBookings itemDtoWithBookings;

    private Item item;

    private Comment comment;

    private CommentDto commentDto;

    private BookingControllerTest bookingControllerTest = new BookingControllerTest();

    @BeforeEach
    void setUp() {
        mvc = MockMvcBuilders
                .standaloneSetup(itemController)
                .build();

        item = new Item();
        item.setId(1L);
        item.setDescription("Example text");
        item.setAvailable(true);
        item.setName("Example");
        item.setOwner(bookingControllerTest.createUser());

        itemDto = new ItemDto();
        itemDto.setId(1L);

        itemDtoWithBookings = new ItemDtoWithBookings();
        itemDtoWithBookings.setId(1L);

        comment = new Comment();
        comment.setId(1L);

        commentDto = new CommentDto();
        commentDto.setId(1L);
        commentDto.setText("Example");
    }

    @Test
    void addItemTest() throws Exception {
        when(itemMapper.toItem(any(ItemDto.class)))
                .thenReturn(item);
        when(itemService.addItem(anyLong(), any(Item.class)))
                .thenReturn(item);
        when(itemMapper.toItemDto(any(Item.class)))
                .thenReturn(itemDto);

        mvc.perform(post("/items")
                        .content(mapper.writeValueAsString(itemDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("X-Sharer-User-Id", 1)
                        .accept(MediaType.APPLICATION_JSON))

                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(itemDto.getId()), Long.class));
    }

    @Test
    void updateItemTest() throws Exception {
        when(itemService.updateItem(anyLong(), anyLong(), any(ItemDto.class)))
                .thenReturn(item);
        when(itemMapper.toItemDto(any(Item.class)))
                .thenReturn(itemDto);

        mvc.perform(patch("/items/1")
                        .content(mapper.writeValueAsString(itemDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("X-Sharer-User-Id", 1)
                        .accept(MediaType.APPLICATION_JSON))

                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(itemDto.getId()), Long.class));
    }

    @Test
    void getItemTest() throws Exception {
        when(itemService.getItemById(anyLong(), anyLong()))
                .thenReturn(item);
        when(itemMapper.toItemDtoWithBookings(any(Item.class)))
                .thenReturn(itemDtoWithBookings);

        mvc.perform(get("/items/1")
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("X-Sharer-User-Id", 1)
                        .accept(MediaType.APPLICATION_JSON))

                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(itemDtoWithBookings.getId()), Long.class));
    }

    @Test
    void getItemTestWithError() throws Exception {
        when(itemService.getItemById(anyLong(), anyLong()))
                .thenThrow(new UnknownIdException("Item not found"));

        mvc.perform(get("/items/1")
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("X-Sharer-User-Id", 1)
                        .accept(MediaType.APPLICATION_JSON))

                .andExpect(status().isNotFound());
    }

    @Test
    void getItemTestWithErrorWithNoMessage() throws Exception {
        when(itemService.getItemById(anyLong(), anyLong()))
                .thenThrow(new UnknownIdException());

        mvc.perform(get("/items/1")
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("X-Sharer-User-Id", 1)
                        .accept(MediaType.APPLICATION_JSON))

                .andExpect(status().isNotFound());
    }

    @Test
    void getItemsByOwnerTest() throws Exception {
        when(itemService.getItemsByOwnerId(anyLong(), any(Pageable.class)))
                .thenReturn(new ArrayList<Item>());

        mvc.perform(get("/items")
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("X-Sharer-User-Id", 1)
                        .accept(MediaType.APPLICATION_JSON))

                .andExpect(status().isOk());
    }

    @Test
    void searchByKeywordTest() throws Exception {
        when(itemService.findItemsByKeyword(anyString(), any(Pageable.class)))
                .thenReturn(new ArrayList<Item>());

        mvc.perform(get("/items/search/?text=text")
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("X-Sharer-User-Id", 1)
                        .accept(MediaType.APPLICATION_JSON))

                .andExpect(status().isOk());
    }

    @Test
    void testAddComment() throws Exception {
        when(itemMapper.toComment(any(CommentDto.class)))
                .thenReturn(comment);
        when(itemService.addComment(anyLong(), anyLong(), any(Comment.class)))
                .thenReturn(comment);
        when(itemMapper.toCommentDto(any(Comment.class)))
                .thenReturn(commentDto);

        mvc.perform(post("/items/1/comment")
                        .content(mapper.writeValueAsString(commentDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("X-Sharer-User-Id", 1)
                        .accept(MediaType.APPLICATION_JSON))

                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(commentDto.getId()), Long.class))
                .andExpect(jsonPath("$.text", is(commentDto.getText()), String.class));
    }


}
