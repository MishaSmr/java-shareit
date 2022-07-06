package ru.practicum.shareit.booking.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import ru.practicum.shareit.booking.Status;

import java.time.LocalDate;

@Data
@AllArgsConstructor
public class BookingDto {
    private long id;
    private LocalDate start;
    private LocalDate end;
    private long itemId;
    private long bookerId;
    private Status status;
}
