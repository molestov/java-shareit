package ru.practicum.shareit.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.json.JsonMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
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
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.item.service.ItemService;
import ru.practicum.shareit.request.controller.ItemRequestController;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.mapper.ItemRequestMapper;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.request.service.ItemRequestService;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;

import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
public class ItemRequestControllerTest {
    @InjectMocks
    private ItemRequestController itemRequestController;

    @Spy
    private ItemRequestMapper itemRequestMapper = Mappers.getMapper(ItemRequestMapper.class);

    @Mock
    private ItemRequestService itemRequestService;

    @Mock
    private ItemService itemService;

    @Spy
    private ItemMapper itemMapper = Mappers.getMapper(ItemMapper.class);

    private final ObjectMapper mapper = JsonMapper.builder()
            .addModule(new JavaTimeModule())
            .build();

    private MockMvc mvc;

    @BeforeEach
    void setUp() {
        mvc = MockMvcBuilders
                .standaloneSetup(itemRequestController)
                .build();
    }

    @Test
    void testItemRequest() throws Exception {
        when(itemRequestService.addItemRequest(anyLong(), any(ItemRequest.class)))
                .thenReturn(createItemRequest());

        mvc.perform(post("/requests")
                        .content(mapper.writeValueAsString(createItemRequestDto()))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("X-Sharer-User-Id", 1)
                        .accept(MediaType.APPLICATION_JSON))

                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(createItemRequestDto().getId()), Long.class));
        verify(itemRequestService, times(1)).addItemRequest(anyLong(), any(ItemRequest.class));
    }

    @Test
    void testGetAllRequests() throws Exception {
        when(itemRequestService.getAllRequests(anyLong(), any(Pageable.class)))
                .thenReturn(new ArrayList<ItemRequest>());

        mvc.perform(get("/requests")
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("X-Sharer-User-Id", 1)
                        .accept(MediaType.APPLICATION_JSON))

                .andExpect(status().isOk());
        verify(itemRequestService, times(1)).getAllRequests(anyLong(), any(Pageable.class));
    }

    @Test
    void testGetAllRequestsWithParameters() throws Exception {
        when(itemRequestService.getAllRequestsWithPages(anyLong(), anyInt(), anyInt()))
                .thenReturn(new ArrayList<ItemRequest>());

        mvc.perform(get("/requests/all")
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("X-Sharer-User-Id", 1)
                        .accept(MediaType.APPLICATION_JSON))

                .andExpect(status().isOk());
        verify(itemRequestService, times(1)).getAllRequestsWithPages(anyLong(), anyInt(), anyInt());
    }

    @Test
    void testGetRequest() throws Exception {
        when(itemRequestService.getRequest(anyLong(), anyLong()))
                .thenReturn(createItemRequest());

        mvc.perform(get("/requests/1")
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("X-Sharer-User-Id", 1)
                        .accept(MediaType.APPLICATION_JSON))

                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(createItemRequestDto().getId()), Long.class));
        verify(itemRequestService, times(1)).getRequest(anyLong(), anyLong());
    }

    private ItemRequest createItemRequest() {
        ItemRequest itemRequest = new ItemRequest();
        itemRequest.setId(1L);
        return itemRequest;
    }

    private ItemRequestDto createItemRequestDto() {
        ItemRequestDto itemRequestDto = new ItemRequestDto();
        itemRequestDto.setId(1L);
        itemRequestDto.setDescription("Example test");
        return itemRequestDto;
    }
}
