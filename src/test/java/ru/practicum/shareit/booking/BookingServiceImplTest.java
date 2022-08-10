package ru.practicum.shareit.booking;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.*;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingExtDto;
import ru.practicum.shareit.exceptions.*;
import ru.practicum.shareit.item.Item;
import ru.practicum.shareit.item.ItemRepository;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserRepository;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;

@SpringBootTest
@RequiredArgsConstructor(onConstructor_ = @Autowired)
class BookingServiceImplTest {

    private final BookingServiceImpl bookingService;
    @MockBean
    UserRepository userRepository;
    @MockBean
    ItemRepository itemRepository;
    @MockBean
    BookingRepository bookingRepository;

    LocalDateTime currentDateTime = LocalDateTime.now();

    User user = new User(11L, "name", "1@1.com");

    Item item = new Item(
            1,
            "Дрель",
            "Просто дрель",
            true,
            user,
            null);

    Booking futureBooking = new Booking(
            2L,
            currentDateTime.plusDays(2),
            currentDateTime.plusDays(3),
            item,
            user,
            Status.APPROVED
    );

    BookingDto bookingDto = BookingMapper.toBookingDto(futureBooking);

    @Test
    void testCreateBookingWithUserIdIsNull() {
        UserNotDefinedException ex = Assertions.assertThrows(
                UserNotDefinedException.class,
                () ->
                        bookingService.create(null, bookingDto));
        Assertions.assertEquals("Пользователь не определен",
                ex.getMessage());
    }

    @Test
    void testCreateBookingFromOwner() {
        when(userRepository.getReferenceById(anyLong()))
                .thenReturn(user);
        when(itemRepository.getReferenceById(anyLong()))
                .thenReturn(item);
        UserNotFoundException ex = Assertions.assertThrows(
                UserNotFoundException.class,
                () ->
                        bookingService.create(11L, bookingDto));
        Assertions.assertEquals("Пользователь - владелец предмета",
                ex.getMessage());
    }

    @Test
    void testCreateBookingWithEndIsBeforeNow() {
        bookingDto.setEnd(LocalDateTime.now().minusDays(1));
        when(userRepository.getReferenceById(anyLong()))
                .thenReturn(user);
        when(itemRepository.getReferenceById(anyLong()))
                .thenReturn(item);
        BookingDateException ex = Assertions.assertThrows(
                BookingDateException.class,
                () ->
                        bookingService.create(22L, bookingDto));
        Assertions.assertEquals("Указаны неверные даты бронирования",
                ex.getMessage());
    }

    @Test
    void testCreateBookingWithStartIsBeforeNow() {
        bookingDto.setStart(LocalDateTime.now().minusDays(1));
        when(userRepository.getReferenceById(anyLong()))
                .thenReturn(user);
        when(itemRepository.getReferenceById(anyLong()))
                .thenReturn(item);
        BookingDateException ex = Assertions.assertThrows(
                BookingDateException.class,
                () ->
                        bookingService.create(22L, bookingDto));
        Assertions.assertEquals("Указаны неверные даты бронирования",
                ex.getMessage());
    }

    @Test
    void testCreateBookingWithWrongStartIsAfterEnd() {
        bookingDto.setStart(bookingDto.getEnd().plusDays(1));
        when(userRepository.getReferenceById(anyLong()))
                .thenReturn(user);
        when(itemRepository.getReferenceById(anyLong()))
                .thenReturn(item);
        BookingDateException ex = Assertions.assertThrows(
                BookingDateException.class,
                () ->
                        bookingService.create(22L, bookingDto));
        Assertions.assertEquals("Указаны неверные даты бронирования",
                ex.getMessage());
    }

    @Test
    void testCreateBookingWithNotAvailableItem() {
        item.setAvailable(false);
        when(userRepository.getReferenceById(anyLong()))
                .thenReturn(user);
        when(itemRepository.getReferenceById(anyLong()))
                .thenReturn(item);
        ItemNotAvailableException ex = Assertions.assertThrows(
                ItemNotAvailableException.class,
                () ->
                        bookingService.create(22L, bookingDto));
        Assertions.assertEquals("Предмет не доступен для аренды",
                ex.getMessage());
    }

    @Test
    void testBookingChangeStatusFromNotOwner() {
        when(userRepository.getReferenceById(anyLong()))
                .thenReturn(user);
        when(bookingRepository.getReferenceById(anyLong()))
                .thenReturn(futureBooking);
        UserIsNotOwnerException ex = Assertions.assertThrows(
                UserIsNotOwnerException.class,
                () ->
                        bookingService.changeStatus(21L, 2L, false));
        Assertions.assertEquals("Пользователь не владелец предмета",
                ex.getMessage());
    }

    @Test
    void testBookingChangeStatusAlreadyChanged() {
        when(userRepository.getReferenceById(anyLong()))
                .thenReturn(user);
        when(bookingRepository.getReferenceById(anyLong()))
                .thenReturn(futureBooking);
        StatusAlreadyChangedException ex = Assertions.assertThrows(
                StatusAlreadyChangedException.class,
                () ->
                        bookingService.changeStatus(11L, 2L, true));
        Assertions.assertEquals("Уже подтверждено", ex.getMessage());
    }

    @Test
    void testBookingGetByIdFromNotOwnerOrNotBooker() {
        when(userRepository.getReferenceById(anyLong()))
                .thenReturn(user);
        when(bookingRepository.getReferenceById(anyLong()))
                .thenReturn(futureBooking);
        BookingNotFoundException ex = Assertions.assertThrows(
                BookingNotFoundException.class,
                () ->
                        bookingService.getById(21L, 2L));
        Assertions.assertEquals("Пользователь не владелец предмета или не автор бронирования",
                ex.getMessage());
    }

    @Test
    void testBookingGetForBookerAll() {
        Pageable pageable = PageRequest.of(0, 10, Sort.by(Sort.Direction.DESC, "start"));
        when(userRepository.getReferenceById(anyLong()))
                .thenReturn(user);
        when(bookingRepository.findByBooker_Id(11L, pageable))
                .thenReturn(Page.empty());
        bookingService.getForBooker(11L, "ALL", 0, 10);
        Mockito.verify(bookingRepository, Mockito.times(1))
                .findByBooker_Id(11L, pageable);
    }

    @Test
    void testBookingGetForBookerCurrent() {
        when(userRepository.getReferenceById(Mockito.anyLong()))
                .thenReturn(user);
        when(bookingRepository.findByBooker_IdAndStartIsBeforeAndEndIsAfter(anyLong(),
                any(LocalDateTime.class), any(LocalDateTime.class), any(Pageable.class)))
                .thenReturn(Page.empty());
        bookingService.getForBooker(11L, "CURRENT", 0, 10);
        Mockito.verify(bookingRepository, Mockito.times(1))
                .findByBooker_IdAndStartIsBeforeAndEndIsAfter(anyLong(),
                        any(LocalDateTime.class), any(LocalDateTime.class), any(Pageable.class));
    }

    @Test
    void testBookingGetForBookerPast() {
        when(userRepository.getReferenceById(Mockito.anyLong()))
                .thenReturn(user);
        when(bookingRepository.findByBooker_IdAndEndIsBefore(anyLong(), any(LocalDateTime.class),
                any(Pageable.class)))
                .thenReturn(Page.empty());
        bookingService.getForBooker(11L, "PAST", 0, 10);
        Mockito.verify(bookingRepository, Mockito.times(1))
                .findByBooker_IdAndEndIsBefore(anyLong(), any(LocalDateTime.class), any(Pageable.class));
    }

    @Test
    void testBookingGetForBookerFuture() {
        when(userRepository.getReferenceById(Mockito.anyLong()))
                .thenReturn(user);
        when(bookingRepository.findByBooker_IdAndStartIsAfter(anyLong(), any(LocalDateTime.class), any(Pageable.class)))
                .thenReturn(Page.empty());
        bookingService.getForBooker(11L, "FUTURE", 0, 10);
        Mockito.verify(bookingRepository, Mockito.times(1))
                .findByBooker_IdAndStartIsAfter(anyLong(), any(LocalDateTime.class), any(Pageable.class));
    }

    @Test
    void testBookingGetForBookerByStateIsEqualsStatus() {
        when(userRepository.getReferenceById(Mockito.anyLong()))
                .thenReturn(user);
        when(bookingRepository.findByBooker_IdAndStatus(anyLong(), any(Status.class), any(Pageable.class)))
                .thenReturn(Page.empty());
        bookingService.getForBooker(11L, "WAITING", 0, 10);
        Mockito.verify(bookingRepository, Mockito.times(1))
                .findByBooker_IdAndStatus(anyLong(), any(Status.class), any(Pageable.class));
    }

    @Test
    void testBookingGetForBookerByErrorState() {
        IncorrectParameterException ex = Assertions.assertThrows(
                IncorrectParameterException.class,
                () ->
                        bookingService.getForBooker(11L, "wrong", 0, 10));
        Assertions.assertEquals("wrong",
                ex.getValue());
    }

    @Test
    void testBookingGetForOwnerForUserWithoutItems() {
        when(userRepository.getReferenceById(anyLong()))
                .thenReturn(user);
        when(itemRepository.findByOwner_Id(anyLong()))
                .thenReturn(Collections.emptyList());
        List<BookingExtDto> bookings = bookingService.getForOwner(11L, "ALL", 0, 10);
        Assertions.assertTrue(bookings.isEmpty());
    }

    @Test
    void testBookingGetForOwnerAll() {
        Pageable pageable = PageRequest.of(0, 10, Sort.by(Sort.Direction.DESC, "start"));
        when(userRepository.getReferenceById(anyLong()))
                .thenReturn(user);
        when(itemRepository.findByOwner_Id(anyLong()))
                .thenReturn(List.of(item));
        when(bookingRepository.getAllForOwner(11L, pageable))
                .thenReturn(Page.empty());
        bookingService.getForOwner(11L, "ALL", 0, 10);
        Mockito.verify(bookingRepository, Mockito.times(1))
                .getAllForOwner(11L, pageable);
    }

    @Test
    void testBookingGetForOwnerCurrent() {
        when(userRepository.getReferenceById(Mockito.anyLong()))
                .thenReturn(user);
        when(itemRepository.findByOwner_Id(anyLong()))
                .thenReturn(List.of(item));
        when(bookingRepository.getCurrentForOwner(anyLong(), any(LocalDateTime.class), any(Pageable.class)))
                .thenReturn(Page.empty());
        bookingService.getForOwner(11L, "CURRENT", 0, 10);
        Mockito.verify(bookingRepository, Mockito.times(1))
                .getCurrentForOwner(anyLong(), any(LocalDateTime.class), any(Pageable.class));
    }

    @Test
    void testBookingGetForOwnerPast() {
        when(userRepository.getReferenceById(Mockito.anyLong()))
                .thenReturn(user);
        when(itemRepository.findByOwner_Id(anyLong()))
                .thenReturn(List.of(item));
        when(bookingRepository.getPastForOwner(anyLong(), any(LocalDateTime.class), any(Pageable.class)))
                .thenReturn(Page.empty());
        bookingService.getForOwner(11L, "PAST", 0, 10);
        Mockito.verify(bookingRepository, Mockito.times(1))
                .getPastForOwner(anyLong(), any(LocalDateTime.class), any(Pageable.class));
    }

    @Test
    void testBookingGetForOwnerFuture() {
        when(userRepository.getReferenceById(Mockito.anyLong()))
                .thenReturn(user);
        when(itemRepository.findByOwner_Id(anyLong()))
                .thenReturn(List.of(item));
        when(bookingRepository.getFutureForOwner(anyLong(), any(LocalDateTime.class), any(Pageable.class)))
                .thenReturn(Page.empty());
        bookingService.getForOwner(11L, "FUTURE", 0, 10);
        Mockito.verify(bookingRepository, Mockito.times(1))
                .getFutureForOwner(anyLong(), any(LocalDateTime.class), any(Pageable.class));
    }

    @Test
    void testBookingGetForOwnerByStateIsEqualsStatus() {
        when(userRepository.getReferenceById(Mockito.anyLong()))
                .thenReturn(user);
        when(itemRepository.findByOwner_Id(anyLong()))
                .thenReturn(List.of(item));
        when(bookingRepository.getAllForOwnerAndStatus(anyLong(), any(Status.class), any(Pageable.class)))
                .thenReturn(Page.empty());
        bookingService.getForOwner(11L, "WAITING", 0, 10);
        Mockito.verify(bookingRepository, Mockito.times(1))
                .getAllForOwnerAndStatus(anyLong(), any(Status.class), any(Pageable.class));
    }

    @Test
    void testBookingGetForOwnerByErrorState() {
        IncorrectParameterException ex = Assertions.assertThrows(
                IncorrectParameterException.class,
                () ->
                        bookingService.getForOwner(11L, "wrong", 0, 10));
        Assertions.assertEquals("wrong",
                ex.getValue());
    }
}