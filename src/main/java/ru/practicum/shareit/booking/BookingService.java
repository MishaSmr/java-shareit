package ru.practicum.shareit.booking;

import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingExtDto;

import java.util.List;

public interface BookingService {

    BookingDto create(Long bookerId, BookingDto bookingDto);

    BookingExtDto changeStatus(Long userId, Long bookingId, Boolean approved);

    BookingExtDto getById(Long userId, Long bookingId);

    List<BookingExtDto> getForBooker(Long bookerId, String state, Integer from, Integer size);

    List<BookingExtDto> getForOwner(Long userId, String state, Integer from, Integer size);
}
