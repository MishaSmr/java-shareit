package ru.practicum.shareit.requests.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import ru.practicum.shareit.item.dto.ItemDto;

import java.time.LocalDateTime;
import java.util.List;

@Data
@AllArgsConstructor
public class ItemRequestExtDto {
    private long id;
    private String description;
    private long requesterId;
    private LocalDateTime created;
    private List<ItemDto> items;
}
