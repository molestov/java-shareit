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
import ru.practicum.shareit.booking.controller.BookingController;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingDtoWithEntities;
import ru.practicum.shareit.booking.mapper.BookingMapper;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.booking.service.BookingService;
import ru.practicum.shareit.error.exception.BookingByOwnerException;
import ru.practicum.shareit.error.exception.EndBeforeStartException;
import ru.practicum.shareit.error.exception.UnavailableItemException;
import ru.practicum.shareit.error.exception.WrongStateException;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.service.ItemService;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.service.UserService;

import java.nio.charset.StandardCharsets;
import java.sql.Timestamp;
import java.time.Instant;
import java.time.LocalDateTime;
import java.util.ArrayList;

import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyBoolean;
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
public class BookingControllerTest {
    @Mock
    private BookingService bookingService;

    @Spy
    private BookingMapper bookingMapper = Mappers.getMapper(BookingMapper.class);

    @Mock
    private ItemService itemService;

    @Mock
    private UserService userService;

    @InjectMocks
    private BookingController bookingController;

    private final ObjectMapper mapper = JsonMapper.builder()
            .addModule(new JavaTimeModule())
            .build();

    private MockMvc mvc;

    @BeforeEach
    void setUp() {
        mvc = MockMvcBuilders
                .standaloneSetup(bookingController)
                .build();
    }

    @Test
    void addBookingTestShouldReturn200() throws Exception {
        Booking booking = createBooking();
        when(bookingService.addBooking(anyLong(), any(Booking.class)))
                .thenReturn(booking);

        mvc.perform(post("/bookings")
                        .content(mapper.writeValueAsString(makeBookingDto()))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("X-Sharer-User-Id", 1)
                        .accept(MediaType.APPLICATION_JSON))

                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(createBooking().getId()), Long.class));
        verify(bookingService, times(1)).addBooking(anyLong(), any(Booking.class));
    }

    @Test
    void addBookingTestShouldReturn400() throws Exception {
        when(bookingService.addBooking(anyLong(), any(Booking.class)))
                .thenThrow(new WrongStateException("Illegal user"));

        mvc.perform(post("/bookings")
                        .content(mapper.writeValueAsString(makeBookingDto()))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("X-Sharer-User-Id", 1)
                        .accept(MediaType.APPLICATION_JSON))

                .andExpect(status().isBadRequest());
        verify(bookingService, times(1)).addBooking(anyLong(), any(Booking.class));
    }

    @Test
    void addBookingTestWithError() throws Exception {
        when(bookingService.addBooking(anyLong(), any(Booking.class)))
                .thenThrow(new UnavailableItemException("Example"));

        mvc.perform(post("/bookings")
                        .content(mapper.writeValueAsString(makeBookingDto()))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("X-Sharer-User-Id", 1)
                        .accept(MediaType.APPLICATION_JSON))

                .andExpect(status().isBadRequest());
        verify(bookingService, times(1)).addBooking(anyLong(), any(Booking.class));
    }

    @Test
    void addBookingTestWithErrorOwnerBooked() throws Exception {
        when(bookingService.addBooking(anyLong(), any(Booking.class)))
                .thenThrow(new BookingByOwnerException("Example"));

        mvc.perform(post("/bookings")
                        .content(mapper.writeValueAsString(makeBookingDto()))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("X-Sharer-User-Id", 1)
                        .accept(MediaType.APPLICATION_JSON))

                .andExpect(status().isNotFound());
        verify(bookingService, times(1)).addBooking(anyLong(), any(Booking.class));
    }

    @Test
    void addBookingTestWithErrorEndBeforeStart() throws Exception {
        when(bookingService.addBooking(anyLong(), any(Booking.class)))
                .thenThrow(new EndBeforeStartException("Example"));

        mvc.perform(post("/bookings")
                        .content(mapper.writeValueAsString(makeBookingDto()))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("X-Sharer-User-Id", 1)
                        .accept(MediaType.APPLICATION_JSON))

                .andExpect(status().isBadRequest());
        verify(bookingService, times(1)).addBooking(anyLong(), any(Booking.class));
    }

    @Test
    void testGetBookingShouldReturn200() throws Exception {
        when(bookingService.getBooking(anyLong(), anyLong()))
                .thenReturn(createBooking());

        mvc.perform(get("/bookings/1")
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("X-Sharer-User-Id", 1)
                        .accept(MediaType.APPLICATION_JSON))

                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(makeBookingDtoWithEntities().getId()), Long.class));
        verify(bookingService, times(1)).getBooking(anyLong(), anyLong());
    }

    @Test
    void shouldReturn200WhenSetBookingStatus() throws Exception {
        BookingDtoWithEntities bookingDtoWithEntities = makeBookingDtoWithEntities();
        Booking booking = createBooking();
        booking.setStatus(BookingStatus.APPROVED);
        bookingDtoWithEntities.setStatus(BookingStatus.APPROVED);
        when(bookingService.setBookingStatus(anyLong(), anyLong(), anyBoolean()))
                .thenReturn(booking);

        mvc.perform(patch("/bookings/1/?approved=true")
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("X-Sharer-User-Id", 1)
                        .accept(MediaType.APPLICATION_JSON))

                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status", is("APPROVED")));
        verify(bookingService, times(1)).setBookingStatus(anyLong(), anyLong(), anyBoolean());
    }

    @Test
    void testGetAllUserBookingsByState() throws Exception {
        when(bookingService.getAllUserBookingsByState(anyLong(), anyString(), any(Pageable.class)))
                .thenReturn(new ArrayList<Booking>());

        mvc.perform(get("/bookings/")
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("X-Sharer-User-Id", 1)
                        .accept(MediaType.APPLICATION_JSON))

                .andExpect(status().isOk());
        verify(bookingService, times(1)).getAllUserBookingsByState(anyLong(), anyString(),
                any(Pageable.class));
    }

    @Test
    void testGetAllOwnerBookingsByState() throws Exception {
        when(bookingService.getAllOwnerBookingsByState(anyLong(), anyString(), any(Pageable.class)))
                .thenReturn(new ArrayList<Booking>());

        mvc.perform(get("/bookings/owner")
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("X-Sharer-User-Id", 1)
                        .accept(MediaType.APPLICATION_JSON))

                .andExpect(status().isOk());
        verify(bookingService, times(1)).getAllOwnerBookingsByState(anyLong(), anyString(),
                any(Pageable.class));
    }

    private Booking createBooking() {
        Booking booking = new Booking();
        booking.setId(1L);
        booking.setStatus(BookingStatus.WAITING);
        booking.setStart(Timestamp.from(Instant.now()));
        booking.setEnd(Timestamp.from(Instant.now().plusSeconds(3600)));
        booking.setItem(createItem());
        booking.setBooker(createUser());
        return booking;
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

    private BookingDto makeBookingDto(Long id, LocalDateTime start, LocalDateTime end, Long itemId, Long bookerId,
                                      BookingStatus status) {
        return new BookingDto(id, start, end, itemId, bookerId, status);
    }

    private BookingDto makeBookingDto() {
        BookingDto bookingDto = new BookingDto(
                1L,
                LocalDateTime.now().plusHours(1),
                LocalDateTime.now().plusHours(12),
                1L,
                1L,
                BookingStatus.WAITING);
        return bookingDto;
    }

    private BookingDtoWithEntities makeBookingDtoWithEntities() {
        BookingDtoWithEntities bookingDtoWithEntities =  new BookingDtoWithEntities();
        bookingDtoWithEntities.setId(1L);
        bookingDtoWithEntities.setStart(LocalDateTime.now().plusHours(1));
        bookingDtoWithEntities.setEnd(LocalDateTime.now().plusHours(12));
        bookingDtoWithEntities.setItem(createItem());
        bookingDtoWithEntities.setBooker(createUser());
        bookingDtoWithEntities.setStatus(BookingStatus.WAITING);
        return bookingDtoWithEntities;
    }
}
