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
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyBoolean;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
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

    @Mock
    private BookingMapper bookingMapper;

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

    private BookingDto bookingDto;

    private BookingDtoWithEntities bookingDtoWithEntities;

    private Booking booking;

    @BeforeEach
    void setUp() {
        mvc = MockMvcBuilders
                .standaloneSetup(bookingController)
                .build();

        booking = createBooking();
        booking.setItem(createItem());
        booking.setBooker(createUser());

        bookingDto = new BookingDto(
                1L,
                LocalDateTime.now().plusHours(1),
                LocalDateTime.now().plusHours(12),
                1L,
                1L,
                BookingStatus.WAITING);

        bookingDtoWithEntities = new BookingDtoWithEntities();
        bookingDtoWithEntities.setId(1L);
        bookingDtoWithEntities.setStart(LocalDateTime.now().plusHours(1));
        bookingDtoWithEntities.setEnd(LocalDateTime.now().plusHours(12));
        bookingDtoWithEntities.setItem(createItem());
        bookingDtoWithEntities.setBooker(createUser());
        bookingDtoWithEntities.setStatus(BookingStatus.WAITING);
    }

    @Test
    void addBookingTestShouldReturn200() throws Exception {
        when(bookingMapper.bookingDtoToBooking(any(BookingDto.class)))
                .thenReturn(booking);
        when(bookingService.addBooking(anyLong(), any(Booking.class)))
                .thenReturn(booking);
        when(bookingMapper.bookingToBookingDtoWithEntities(any(Booking.class)))
                .thenReturn(bookingDtoWithEntities);

        mvc.perform(post("/bookings")
                        .content(mapper.writeValueAsString(bookingDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("X-Sharer-User-Id", 1)
                        .accept(MediaType.APPLICATION_JSON))

                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(booking.getId()), Long.class));
    }

    @Test
    void addBookingTestShouldReturn400() throws Exception {
        when(bookingMapper.bookingDtoToBooking(any(BookingDto.class)))
                .thenReturn(booking);
        when(bookingService.addBooking(anyLong(), any(Booking.class)))
                .thenThrow(new WrongStateException("Illegal user"));

        mvc.perform(post("/bookings")
                        .content(mapper.writeValueAsString(bookingDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("X-Sharer-User-Id", 1)
                        .accept(MediaType.APPLICATION_JSON))

                .andExpect(status().isBadRequest());
    }

    @Test
    void addBookingTestWithError() throws Exception {
        when(bookingMapper.bookingDtoToBooking(any(BookingDto.class)))
                .thenReturn(booking);
        when(bookingService.addBooking(anyLong(), any(Booking.class)))
                .thenThrow(new UnavailableItemException("Example"));

        mvc.perform(post("/bookings")
                        .content(mapper.writeValueAsString(bookingDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("X-Sharer-User-Id", 1)
                        .accept(MediaType.APPLICATION_JSON))

                .andExpect(status().isBadRequest());
    }

    @Test
    void addBookingTestWithErrorOwnerBooked() throws Exception {
        when(bookingMapper.bookingDtoToBooking(any(BookingDto.class)))
                .thenReturn(booking);
        when(bookingService.addBooking(anyLong(), any(Booking.class)))
                .thenThrow(new BookingByOwnerException("Example"));

        mvc.perform(post("/bookings")
                        .content(mapper.writeValueAsString(bookingDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("X-Sharer-User-Id", 1)
                        .accept(MediaType.APPLICATION_JSON))

                .andExpect(status().isNotFound());
    }

    @Test
    void addBookingTestWithErrorEndBeforeStart() throws Exception {
        when(bookingMapper.bookingDtoToBooking(any(BookingDto.class)))
                .thenReturn(booking);
        when(bookingService.addBooking(anyLong(), any(Booking.class)))
                .thenThrow(new EndBeforeStartException("Example"));

        mvc.perform(post("/bookings")
                        .content(mapper.writeValueAsString(bookingDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("X-Sharer-User-Id", 1)
                        .accept(MediaType.APPLICATION_JSON))

                .andExpect(status().isBadRequest());
    }

    @Test
    void testGetBookingShouldReturn200() throws Exception {
        when(bookingService.getBooking(anyLong(), anyLong()))
                .thenReturn(booking);
        when(bookingMapper.bookingToBookingDtoWithEntities(any(Booking.class)))
                .thenReturn(bookingDtoWithEntities);

        mvc.perform(get("/bookings/1")
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("X-Sharer-User-Id", 1)
                        .accept(MediaType.APPLICATION_JSON))

                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(bookingDtoWithEntities.getId()), Long.class));
    }

    @Test
    void shouldReturn200WhenSetBookingStatus() throws Exception {
        booking.setStatus(BookingStatus.APPROVED);
        bookingDtoWithEntities.setStatus(BookingStatus.APPROVED);
        when(bookingService.setBookingStatus(anyLong(), anyLong(), anyBoolean()))
                .thenReturn(booking);
        when(bookingMapper.bookingToBookingDtoWithEntities(any(Booking.class)))
                .thenReturn(bookingDtoWithEntities);

        mvc.perform(patch("/bookings/1/?approved=true")
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("X-Sharer-User-Id", 1)
                        .accept(MediaType.APPLICATION_JSON))

                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status", is("APPROVED")));
    }

    @Test
    void testGetAllUserBookingsByState() throws Exception {
        when(bookingService.getAllUserBookingsByState(anyLong(), anyString(), any(Pageable.class)))
                .thenReturn(new ArrayList<Booking>());
        when(bookingMapper.toListDtoWithEntities(anyList()))
                .thenReturn(new ArrayList<BookingDtoWithEntities>());

        mvc.perform(get("/bookings/")
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("X-Sharer-User-Id", 1)
                        .accept(MediaType.APPLICATION_JSON))

                .andExpect(status().isOk());
    }

    @Test
    void testGetAllUserBookingsByStateWithError() throws Exception {

        mvc.perform(get("/bookings/?from=-1&size=1")
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("X-Sharer-User-Id", 1)
                        .accept(MediaType.APPLICATION_JSON))

                .andExpect(status().isBadRequest())
                .andExpect(result -> assertEquals("From cannot be less then 0",
                        result.getResolvedException().getMessage()));
    }

    @Test
    void testGetAllUserBookingsByStateWithError2() throws Exception {

        mvc.perform(get("/bookings/?from=0&size=0")
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("X-Sharer-User-Id", 1)
                        .accept(MediaType.APPLICATION_JSON))

                .andExpect(status().isBadRequest())
                .andExpect(result -> assertEquals("Size cannot be less then 1",
                        result.getResolvedException().getMessage()));
    }

    @Test
    void testGetAllOwnerBookingsByState() throws Exception {
        when(bookingService.getAllOwnerBookingsByState(anyLong(), anyString(), any(Pageable.class)))
                .thenReturn(new ArrayList<Booking>());
        when(bookingMapper.toListDtoWithEntities(anyList()))
                .thenReturn(new ArrayList<BookingDtoWithEntities>());

        mvc.perform(get("/bookings/owner")
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("X-Sharer-User-Id", 1)
                        .accept(MediaType.APPLICATION_JSON))

                .andExpect(status().isOk());
    }

    @Test
    void testGetAllOwnerBookingsByStateWithError() throws Exception {

        mvc.perform(get("/bookings/owner/?from=-1&size=1")
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("X-Sharer-User-Id", 1)
                        .accept(MediaType.APPLICATION_JSON))

                .andExpect(status().isBadRequest())
                .andExpect(result -> assertEquals("From cannot be less then 0",
                        result.getResolvedException().getMessage()));
    }

    @Test
    void testGetAllOwnerBookingsByStateWithError2() throws Exception {

        mvc.perform(get("/bookings/owner/?from=0&size=0")
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("X-Sharer-User-Id", 1)
                        .accept(MediaType.APPLICATION_JSON))

                .andExpect(status().isBadRequest())
                .andExpect(result -> assertEquals("Size cannot be less then 1",
                        result.getResolvedException().getMessage()));
    }

    private Booking createBooking() {
        Booking booking = new Booking();
        booking.setId(1L);
        booking.setStatus(BookingStatus.WAITING);
        booking.setStart(Timestamp.from(Instant.now()));
        booking.setEnd(Timestamp.from(Instant.now().plusSeconds(3600)));
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
}
