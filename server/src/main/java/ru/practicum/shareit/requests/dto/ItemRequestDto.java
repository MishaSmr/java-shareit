package ru.practicum.shareit.requests.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
public class ItemRequestDto {
    private long id;
    private String description;
    private Long requesterId;
    private LocalDateTime created;
}
