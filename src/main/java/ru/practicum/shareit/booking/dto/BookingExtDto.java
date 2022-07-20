package ru.practicum.shareit.booking.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import ru.practicum.shareit.booking.Status;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.user.dto.UserDto;

import java.time.LocalDate;

@Data
@AllArgsConstructor
public class BookingExtDto {
    private long id;
    private LocalDate start;
    private LocalDate end;
    private ItemDto item;
    private UserDto booker;
    private Status status;
}
