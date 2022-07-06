package ru.practicum.shareit.booking;

import lombok.RequiredArgsConstructor;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserRepository;

@RequiredArgsConstructor
public class BookingMapper {

    private static UserRepository userRepository;

    public static BookingDto toBookingDto(Booking booking) {
        return new BookingDto(
                booking.getId(),
                booking.getStart(),
                booking.getEnd(),
                booking.getItem().getId(),
                booking.getBooker().getId(),
                booking.getStatus()
        );
    }

    //Пока такой метод не нужен
    /*public static Booking toBooking(BookingDto bookingDto, long bookerId) {
        return new Booking(
                bookingDto.getId(),
                bookingDto.getStart(),
                bookingDto.getEnd(),
                itemRepository.get(bookingDto.getItemId()),
                userRepository.get(bookerId),
                bookingDto.getStatus()
                );
    }*/
}
