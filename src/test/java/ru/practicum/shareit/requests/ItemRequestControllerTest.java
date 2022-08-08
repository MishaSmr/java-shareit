package ru.practicum.shareit.requests;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.requests.dto.ItemRequestDto;
import ru.practicum.shareit.requests.dto.ItemRequestExtDto;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;

import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = ItemRequestController.class)
class ItemRequestControllerTest {

    @Autowired
    ObjectMapper mapper;
    @MockBean
    ItemRequestService itemRequestService;
    @Autowired
    private MockMvc mvc;

    ItemRequestDto itemRequestDto = new ItemRequestDto(
            1L,
            "Description",
            10L,
            LocalDateTime.MIN
    );
    ItemRequestExtDto itemRequestExtDto = new ItemRequestExtDto(
            1L,
            "Description",
            10L,
            LocalDateTime.MIN,
            Collections.emptyList()
    );

    @Test
    void testCreateItemRequest() throws Exception {
        when(itemRequestService.create(any(), any()))
                .thenReturn(itemRequestDto);

        mvc.perform(post("/requests")
                        .header("X-Sharer-User-Id", 3L)
                        .content(mapper.writeValueAsString(itemRequestDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(itemRequestDto.getId()), Long.class))
                .andExpect(jsonPath("$.description", is(itemRequestDto.getDescription())))
                .andExpect(jsonPath("$.requesterId", is(itemRequestDto.getRequesterId()), Long.class));
    }

    @Test
    void testGetItemRequestsForUser() throws Exception {
        when(itemRequestService.getForUser(any()))
                .thenReturn(List.of(itemRequestExtDto));

        mvc.perform(get("/requests")
                        .header("X-Sharer-User-Id", 3L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.[0].id", is(itemRequestExtDto.getId()), Long.class))
                .andExpect(jsonPath("$.[0].description", is(itemRequestExtDto.getDescription())))
                .andExpect(jsonPath("$.[0].requesterId", is(itemRequestExtDto.getRequesterId()), Long.class));
    }

    @Test
    void testGetAllItemRequests() throws Exception {
        when(itemRequestService.getAll(any(), any(), any()))
                .thenReturn(List.of(itemRequestExtDto));

        mvc.perform(get("/requests/all")
                        .header("X-Sharer-User-Id", 3L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.[0].id", is(itemRequestExtDto.getId()), Long.class))
                .andExpect(jsonPath("$.[0].description", is(itemRequestExtDto.getDescription())))
                .andExpect(jsonPath("$.[0].requesterId", is(itemRequestExtDto.getRequesterId()), Long.class));
    }

    @Test
    void testGetItemRequestById() throws Exception {
        when(itemRequestService.getById(any(), any()))
                .thenReturn(itemRequestExtDto);

        mvc.perform(get("/requests/10")
                        .header("X-Sharer-User-Id", 3L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(itemRequestExtDto.getId()), Long.class))
                .andExpect(jsonPath("$.description", is(itemRequestExtDto.getDescription())))
                .andExpect(jsonPath("$.requesterId", is(itemRequestExtDto.getRequesterId()), Long.class));
    }
}