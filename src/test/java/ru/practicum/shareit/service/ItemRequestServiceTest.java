package ru.practicum.shareit.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.practicum.shareit.error.exception.IllegalUserException;
import ru.practicum.shareit.error.exception.UnknownIdException;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.misc.OffsetBasedPageRequest;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.request.repository.RequestRepository;
import ru.practicum.shareit.request.service.ItemRequestService;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class ItemRequestServiceTest {
    @InjectMocks
    private ItemRequestService itemRequestService;

    @Mock
    private RequestRepository requestRepository;

    @Mock
    private UserRepository userRepository;

    private ItemRequest itemRequest;

    private User user;

    @BeforeEach
    public void setup() {
        itemRequest = new ItemRequest();
        itemRequest.setId(1L);
        itemRequest.setRequestor(createUser());
        user = createUser();
    }

    @Test
    void testAddItemRequest() {
        when(userRepository.findById(anyLong()))
                .thenReturn(Optional.of(user));
        when(requestRepository.save(any(ItemRequest.class)))
                .thenReturn(itemRequest);

        ItemRequest savedItemRequest = itemRequestService.addItemRequest(1L, itemRequest);
        assertTrue(savedItemRequest.getId() == 1L);
    }

    @Test
    void testGetAllRequests() {
        when(userRepository.existsById(anyLong()))
                .thenReturn(true);
        when(requestRepository.findAllByRequestorId(anyLong()))
                .thenReturn(new ArrayList<ItemRequest>());

        List<ItemRequest> savedItemRequest = itemRequestService.getAllRequests(1L,
                new OffsetBasedPageRequest(0, 9999));
        assertNotNull(savedItemRequest);
    }

    @Test
    void testGetAllRequestsWithError1() {
        when(userRepository.existsById(anyLong()))
                .thenReturn(false);

        UnknownIdException exception = assertThrows(UnknownIdException.class,
                () -> itemRequestService.getAllRequests(1L, new OffsetBasedPageRequest(0, 9999)));

        assertEquals("User not found", exception.getMessage());
    }

    @Test
    void testGetRequestWithError() {
        when(userRepository.existsById(anyLong()))
                .thenReturn(false);

        IllegalUserException exception = assertThrows(IllegalUserException.class,
                () -> itemRequestService.getRequest(1L, 1L));

        assertEquals("No such user", exception.getMessage());
    }





    @Test
    void testGetRequest() {
        when(userRepository.existsById(anyLong()))
                .thenReturn(true);
        when(requestRepository.findById(anyLong()))
                .thenReturn(Optional.of(itemRequest));

        ItemRequest savedItemRequest = itemRequestService.getRequest(1L, 1L);
        assertTrue(savedItemRequest.getId() == 1L);
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
        item.setName("Example");
        item.setDescription("Example text");
        item.setAvailable(true);
        return item;
    }
}
