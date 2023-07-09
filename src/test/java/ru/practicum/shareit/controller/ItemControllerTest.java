package ru.practicum.shareit.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.json.JsonMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mapstruct.factory.Mappers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.mapper.BookingMapper;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.service.BookingService;
import ru.practicum.shareit.error.exception.UnknownIdException;
import ru.practicum.shareit.item.controller.ItemController;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemDtoWithBookings;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.service.ItemService;
import ru.practicum.shareit.user.model.User;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Optional;

import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
public class ItemControllerTest {
    @Mock
    private ItemService itemService;

    @Mock
    private BookingService bookingService;

    @Spy
    private ItemMapper itemMapper = Mappers.getMapper(ItemMapper.class);

    @Spy
    private BookingMapper bookingMapper = Mappers.getMapper(BookingMapper.class);

    @InjectMocks
    private ItemController itemController;

    private final ObjectMapper mapper = JsonMapper.builder()
            .addModule(new JavaTimeModule())
            .build();

    private MockMvc mvc;

    private BookingControllerTest bookingControllerTest = new BookingControllerTest();

    @BeforeEach
    void setUp() {
        mvc = MockMvcBuilders
                .standaloneSetup(itemController)
                .build();
    }

    @Test
    void addItemTest() throws Exception {
        when(itemService.addItem(anyLong(), any(Item.class)))
                .thenReturn(createItem());

        mvc.perform(post("/items")
                        .content(mapper.writeValueAsString(createItemDto()))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("X-Sharer-User-Id", 1)
                        .accept(MediaType.APPLICATION_JSON))

                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(createItemDto().getId()), Long.class));
        verify(itemService, times(1)).addItem(anyLong(), any(Item.class));
    }

    @Test
    void updateItemTest() throws Exception {
        when(itemService.updateItem(anyLong(), anyLong(), any(ItemDto.class)))
                .thenReturn(createItem());

        mvc.perform(patch("/items/1")
                        .content(mapper.writeValueAsString(createItemDto()))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("X-Sharer-User-Id", 1)
                        .accept(MediaType.APPLICATION_JSON))

                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(createItemDto().getId()), Long.class));
        verify(itemService, times(1)).updateItem(anyLong(), anyLong(), any(ItemDto.class));
    }

    @Test
    void getItemTest() throws Exception {
        when(itemService.getItemById(anyLong(), anyLong()))
                .thenReturn(createItem());

        mvc.perform(get("/items/1")
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("X-Sharer-User-Id", 1)
                        .accept(MediaType.APPLICATION_JSON))

                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(createItemDtoWithBookings().getId()), Long.class));
        verify(itemService, times(1)).getItemById(anyLong(), anyLong());
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
        verify(itemService, times(1)).getItemById(anyLong(), anyLong());
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
        verify(itemService, times(1)).getItemById(anyLong(), anyLong());
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
        verify(itemService, times(1)).getItemsByOwnerId(anyLong(), any(Pageable.class));
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
        verify(itemService, times(1)).findItemsByKeyword(anyString(), any(Pageable.class));
    }

    @Test
    void testAddComment() throws Exception {
        when(itemService.addComment(anyLong(), anyLong(), any(Comment.class)))
                .thenReturn(createComment());

        mvc.perform(post("/items/1/comment")
                        .content(mapper.writeValueAsString(createCommentDto()))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("X-Sharer-User-Id", 1)
                        .accept(MediaType.APPLICATION_JSON))

                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(createCommentDto().getId()), Long.class))
                .andExpect(jsonPath("$.text", is(createCommentDto().getText()), String.class));
        verify(itemService, times(1)).addComment(anyLong(), anyLong(), any(Comment.class));
    }

    @Test
    void testSetBookings() {
        ItemDtoWithBookings itemDtoWithBookings = new ItemDtoWithBookings();
        itemDtoWithBookings.setId(1L);
        when(bookingService.getLastBooking(anyLong()))
                .thenReturn(Optional.of(new Booking()));
        when(bookingService.getNextBooking(anyLong()))
                .thenReturn(Optional.of(new Booking()));
        when(bookingMapper.toBookingDto(any(Booking.class)))
                .thenReturn(new BookingDto());

        ItemDtoWithBookings savedBooking = itemController.setBookings(itemDtoWithBookings);

        Assertions.assertNotNull(savedBooking);
    }

    protected User createUser() {
        User user = new User();
        user.setId(1L);
        user.setName("Example");
        user.setEmail("examle@example.com");
        return user;
    }

    private Item createItem() {
        Item item = new Item();
        item.setId(1L);
        item.setDescription("Example text");
        item.setAvailable(true);
        item.setName("Example");
        item.setOwner(bookingControllerTest.createUser());
        return item;
    }

    private ItemDto createItemDto() {
        ItemDto itemDto = new ItemDto();
        itemDto.setId(1L);
        return itemDto;
    }

    private ItemDtoWithBookings createItemDtoWithBookings() {
        ItemDtoWithBookings itemDtoWithBookings = new ItemDtoWithBookings();
        itemDtoWithBookings.setId(1L);
        return itemDtoWithBookings;
    }

    private Comment createComment() {
        Comment comment = new Comment();
        comment.setId(1L);
        comment.setText("Example");
        comment.setItem(createItem());
        comment.setAuthor(createUser());
        return comment;
    }

    private CommentDto createCommentDto() {
        CommentDto commentDto = new CommentDto();
        commentDto.setId(1L);
        commentDto.setText("Example");
        return commentDto;
    }
}
