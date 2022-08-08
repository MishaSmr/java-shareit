package ru.practicum.shareit.booking;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingExtDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.user.dto.UserDto;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.List;

import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@WebMvcTest(controllers = BookingController.class)
class BookingControllerTest {

    @Autowired
    ObjectMapper mapper;
    @MockBean
    BookingService bookingService;
    @Autowired
    private MockMvc mvc;

    LocalDateTime currentDateTime = LocalDateTime.now();
    BookingDto bookingDto = new BookingDto(
            1L,
            currentDateTime.plusDays(2),
            currentDateTime.plusDays(3),
            2L,
            3L,
            Status.APPROVED
    );
    BookingExtDto bookingExtDto = new BookingExtDto(
            1L,
            currentDateTime.plusDays(2),
            currentDateTime.plusDays(3),
            new ItemDto(
                    2L,
                    "Name",
                    "Description",
                    true,
                    null,
                    1L
            ),
            new UserDto(3L, "Name", "1@1.ru"),
            Status.APPROVED
    );

    @Test
    void testCreateBooking() throws Exception {
        when(bookingService.create(any(), any()))
                .thenReturn(bookingDto);

        mvc.perform(post("/bookings")
                        .header("X-Sharer-User-Id", 3L)
                        .content(mapper.writeValueAsString(bookingDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(bookingDto.getId()), Long.class))
                .andExpect(jsonPath("$.itemId", is(bookingDto.getItemId()), Long.class))
                .andExpect(jsonPath("$.bookerId", is(bookingDto.getBookerId()), Long.class))
                .andExpect(jsonPath("$.status", is(bookingDto.getStatus().toString())));
    }

    @Test
    void testChangeBookingStatus() throws Exception {
        when(bookingService.changeStatus(any(), any(), any()))
                .thenReturn(bookingExtDto);

        mvc.perform(patch("/bookings/10?approved=false")
                        .header("X-Sharer-User-Id", 3L)
                        .content(mapper.writeValueAsString(bookingDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(bookingExtDto.getId()), Long.class))
                .andExpect(jsonPath("$.item.id", is(bookingExtDto.getItem().getId()), Long.class))
                .andExpect(jsonPath("$.booker.id", is(bookingExtDto.getBooker().getId()), Long.class))
                .andExpect(jsonPath("$.status", is(bookingExtDto.getStatus().toString())));
    }

    @Test
    void testGetBookingById() throws Exception {
        when(bookingService.getById(any(), any()))
                .thenReturn(bookingExtDto);

        mvc.perform(get("/bookings/10")
                        .header("X-Sharer-User-Id", 3L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(bookingExtDto.getId()), Long.class))
                .andExpect(jsonPath("$.item.id", is(bookingExtDto.getItem().getId()), Long.class))
                .andExpect(jsonPath("$.booker.id", is(bookingExtDto.getBooker().getId()), Long.class))
                .andExpect(jsonPath("$.status", is(bookingExtDto.getStatus().toString())));
    }

    @Test
    void testGetBookingsForBooker() throws Exception {
        when(bookingService.getForBooker(any(), any(), any(), any()))
                .thenReturn(List.of(bookingExtDto));

        mvc.perform(get("/bookings?state=ALL&from=0&size=10")
                        .header("X-Sharer-User-Id", 3L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.[0].id", is(bookingExtDto.getId()), Long.class))
                .andExpect(jsonPath("$.[0].item.id", is(bookingExtDto.getItem().getId()), Long.class))
                .andExpect(jsonPath("$.[0].booker.id", is(bookingExtDto.getBooker().getId()), Long.class))
                .andExpect(jsonPath("$.[0].status", is(bookingExtDto.getStatus().toString())));
    }

    @Test
    void testGetBookingsForOwner() throws Exception {
        when(bookingService.getForOwner(any(), any(), any(), any()))
                .thenReturn(List.of(bookingExtDto));

        mvc.perform(get("/bookings/owner?state=ALL&from=0&size=10")
                        .header("X-Sharer-User-Id", 3L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.[0].id", is(bookingExtDto.getId()), Long.class))
                .andExpect(jsonPath("$.[0].item.id", is(bookingExtDto.getItem().getId()), Long.class))
                .andExpect(jsonPath("$.[0].booker.id", is(bookingExtDto.getBooker().getId()), Long.class))
                .andExpect(jsonPath("$.[0].status", is(bookingExtDto.getStatus().toString())));
    }
}