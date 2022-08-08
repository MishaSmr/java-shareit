package ru.practicum.shareit.item.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import java.time.LocalDate;

@Data
@AllArgsConstructor
public class CommentDto {
    private Long id;
    private String text;
    private String authorName;
    LocalDate created;
}
