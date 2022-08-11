package ru.practicum.shareit.item;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemExtDto;

import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.util.Collections;
import java.util.List;

import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@WebMvcTest(controllers = ItemController.class)
class ItemControllerTest {

    @Autowired
    ObjectMapper mapper;
    @MockBean
    ItemService itemService;
    @Autowired
    private MockMvc mvc;

    ItemDto itemDto = new ItemDto(
            1L,
            "Name",
            "Description",
            true,
            null,
            1L
    );
    ItemExtDto itemExtDto = new ItemExtDto(
            1L,
            "Name",
            "Description",
            true,
            null,
            1L,
            null,
            null,
            Collections.emptyList()
    );

    CommentDto commentDto = new CommentDto(
            1L,
            "Text",
            "Name",
            LocalDate.MIN
    );

    @Test
    void testCreateItem() throws Exception {
        when(itemService.create(any(), any()))
                .thenReturn(itemDto);

        mvc.perform(post("/items")
                        .header("X-Sharer-User-Id", 3L)
                        .content(mapper.writeValueAsString(itemDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(itemDto.getId()), Long.class))
                .andExpect(jsonPath("$.name", is(itemDto.getName())))
                .andExpect(jsonPath("$.available", is(itemDto.getAvailable())));
    }

    @Test
    void testUpdateItem() throws Exception {
        when(itemService.update(any(), any(), any()))
                .thenReturn(itemDto);

        mvc.perform(patch("/items/10")
                        .header("X-Sharer-User-Id", 3L)
                        .content(mapper.writeValueAsString(itemDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(itemDto.getId()), Long.class))
                .andExpect(jsonPath("$.name", is(itemDto.getName())))
                .andExpect(jsonPath("$.available", is(itemDto.getAvailable())));
    }

    @Test
    void testGetItem() throws Exception {
        when(itemService.getItem(any(), any()))
                .thenReturn(itemExtDto);

        mvc.perform(get("/items/10")
                        .header("X-Sharer-User-Id", 3L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(itemExtDto.getId()), Long.class))
                .andExpect(jsonPath("$.name", is(itemExtDto.getName())))
                .andExpect(jsonPath("$.available", is(itemExtDto.getAvailable())));
    }

    @Test
    void testGetAllItemsForUser() throws Exception {
        when(itemService.getAllForUser(any(), any(), any()))
                .thenReturn(List.of(itemExtDto));

        mvc.perform(get("/items?from=0&size=10")
                        .header("X-Sharer-User-Id", 3L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.[0].id", is(itemExtDto.getId()), Long.class))
                .andExpect(jsonPath("$.[0].name", is(itemExtDto.getName())))
                .andExpect(jsonPath("$.[0].available", is(itemExtDto.getAvailable())));
    }

    @Test
    void testSearchItems() throws Exception {
        when(itemService.search(any(), any(), any()))
                .thenReturn(List.of(itemDto));

        mvc.perform(get("/items/search?text=text&from=0&size=10")
                        .header("X-Sharer-User-Id", 3L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.[0].id", is(itemDto.getId()), Long.class))
                .andExpect(jsonPath("$.[0].name", is(itemDto.getName())))
                .andExpect(jsonPath("$.[0].available", is(itemDto.getAvailable())));
    }

    @Test
    void testAddComment() throws Exception {
        when(itemService.createComment(any(), any(), any()))
                .thenReturn(commentDto);

        mvc.perform(post("/items/10/comment")
                        .header("X-Sharer-User-Id", 3L)
                        .content(mapper.writeValueAsString(commentDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(commentDto.getId()), Long.class))
                .andExpect(jsonPath("$.text", is(commentDto.getText())))
                .andExpect(jsonPath("$.authorName", is(commentDto.getAuthorName())));
    }
}