package ru.practicum.shareit.booking;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingExtDto;

import java.util.List;

@RestController
@RequestMapping(path = "/bookings")
@RequiredArgsConstructor
public class BookingController {

    private final BookingService bookingService;

    @PostMapping
    public BookingDto create(@RequestHeader("X-Sharer-User-Id") Long bookerId,
                             @RequestBody BookingDto bookingDto) {
        return bookingService.create(bookerId, bookingDto);
    }

    @PatchMapping("/{bookingId}")
    public BookingExtDto changeStatus(@RequestHeader("X-Sharer-User-Id") Long userId,
                                      @PathVariable Long bookingId,
                                      @RequestParam Boolean approved) {
        return bookingService.changeStatus(userId, bookingId, approved);
    }

    @GetMapping("/{bookingId}")
    public BookingExtDto getById(@RequestHeader("X-Sharer-User-Id") Long userId,
                                 @PathVariable Long bookingId) {
        return bookingService.getById(userId, bookingId);
    }

    @GetMapping
    public List<BookingExtDto> getForBooker(@RequestHeader("X-Sharer-User-Id") Long bookerId,
                                            @RequestParam(defaultValue = "ALL") String state,
                                            @RequestParam(defaultValue = "0") Integer from,
                                            @RequestParam(defaultValue = "10") Integer size) {
        return bookingService.getForBooker(bookerId, state, from, size);
    }

    @GetMapping("/owner")
    public List<BookingExtDto> getForOwner(@RequestHeader("X-Sharer-User-Id") Long userId,
                                           @RequestParam(defaultValue = "ALL") String state,
                                           @RequestParam(defaultValue = "0") Integer from,
                                           @RequestParam(defaultValue = "10") Integer size) {
        return bookingService.getForOwner(userId, state, from, size);
    }
}
