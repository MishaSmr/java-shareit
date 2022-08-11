package ru.practicum.shareit.booking;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingExtDto;
import ru.practicum.shareit.item.Item;
import ru.practicum.shareit.item.ItemRepository;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserRepository;

import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;


@SpringBootTest
@AutoConfigureTestDatabase
@Transactional
@RequiredArgsConstructor(onConstructor_ = @Autowired)
class BookingServiceImplIntegrationTest {

    private final BookingServiceImpl bookingService;
    private final UserRepository userRepository;
    private final ItemRepository itemRepository;
    LocalDateTime currentDateTime = LocalDateTime.now();

    User user = new User(1L, "name", "1@1.com");
    User booker = new User(2L, "name2", "2@2.com");

    Item item = new Item(
            1,
            "Дрель",
            "Просто дрель",
            true,
            user,
            null);

    BookingDto bookingDto;

    @BeforeEach
    public void beforeEach() {
        long userId = userRepository.save(user).getId();
        long bookerId = userRepository.save(booker).getId();
        user.setId(userId);
        booker.setId(bookerId);
        long itemId = itemRepository.save(item).getId();
        item.setId(itemId);
        bookingDto = new BookingDto(
                1L,
                currentDateTime.plusDays(1),
                currentDateTime.plusDays(2),
                itemId,
                bookerId,
                Status.APPROVED
        );
    }

    @Test
    void testCreateBooking() {
        Long bookingId = bookingService.create(booker.getId(), bookingDto).getId();
        BookingExtDto testBookingDto = bookingService.getById(booker.getId(), bookingId);
        assertThat(testBookingDto.getBooker().getId()).isEqualTo(booker.getId());
        assertThat(testBookingDto.getItem().getId()).isEqualTo(item.getId());
    }

    @Test
    void testChangeStatus() {
        Long bookingId = bookingService.create(booker.getId(), bookingDto).getId();
        bookingService.changeStatus(user.getId(), bookingId, false);
        BookingExtDto testBookingDto = bookingService.getById(booker.getId(), bookingId);
        assertThat(testBookingDto.getStatus()).isEqualTo(Status.REJECTED);
    }

    @Test
    void testGetBooking() {
        Long bookingId = bookingService.create(booker.getId(), bookingDto).getId();
        BookingExtDto testBookingDto = bookingService.getById(user.getId(), bookingId);
        assertThat(testBookingDto.getBooker().getId()).isEqualTo(booker.getId());
    }

    @Test
    void testGetBookingForBooker() {
        Long bookingId = bookingService.create(booker.getId(), bookingDto).getId();
        List<BookingExtDto> testBookingsDto = bookingService.getForBooker(booker.getId(), "FUTURE", 0, 10);
        assertThat(testBookingsDto.get(0).getId()).isEqualTo(bookingId);
    }

    @Test
    void testGetBookingForOwner() {
        Long bookingId = bookingService.create(booker.getId(), bookingDto).getId();
        List<BookingExtDto> testBookingsDto = bookingService.getForOwner(user.getId(), "FUTURE", 0, 10);
        assertThat(testBookingsDto.get(0).getId()).isEqualTo(bookingId);
    }
}