package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import ru.practicum.shareit.booking.Booking;
import ru.practicum.shareit.booking.BookingRepository;
import ru.practicum.shareit.exceptions.*;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserRepository;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;

import static org.mockito.Mockito.when;

@SpringBootTest
@RequiredArgsConstructor(onConstructor_ = @Autowired)
class ItemServiceImplTest {

    private final ItemServiceImpl itemService;

    @MockBean
    UserRepository userRepository;
    @MockBean
    ItemRepository itemRepository;
    @MockBean
    BookingRepository bookingRepository;

    User user = new User(11L, "name", "1@1.com");
    ItemDto itemDto = new ItemDto(
            1,
            "Дрель",
            "Просто дрель",
            true,
            null,
            11L
    );
    Item item = new Item(
            1,
            "Дрель",
            "Просто дрель",
            true,
            user,
            null);

    CommentDto commentDto = new CommentDto(
            1L,
            "Text",
            "Name",
            LocalDate.now()
    );

    @Test
    void testCreateItemWithUserIdIsNull() {
        UserNotDefinedException ex = Assertions.assertThrows(
                UserNotDefinedException.class,
                () ->
                        itemService.create(null, itemDto));
        Assertions.assertEquals("Пользователь не определен",
                ex.getMessage());
    }

    @Test
    void testCreateItemWithUserNotFound() {
        when(userRepository.getReferenceById(Mockito.anyLong()))
                .thenThrow(new UserNotFoundException("Пользователь c таким id не найден."));
        UserNotFoundException ex = Assertions.assertThrows(
                UserNotFoundException.class,
                () ->
                        itemService.create(55L, itemDto));
        Assertions.assertEquals("Пользователь c таким id не найден.",
                ex.getMessage());
    }

    @Test
    void testCreateItemWithEmptyName() {
        item.setName("");
        ValidationException ex = Assertions.assertThrows(
                ValidationException.class,
                () ->
                        itemService.getValidator().validateItem(item));
        Assertions.assertEquals("Ошибка валидации",
                ex.getMessage());
    }

    @Test
    void testCreateItemWithEmptyDescription() {
        item.setDescription("");
        ValidationException ex = Assertions.assertThrows(
                ValidationException.class,
                () ->
                        itemService.getValidator().validateItem(item));
        Assertions.assertEquals("Ошибка валидации",
                ex.getMessage());
    }

    @Test
    void testCreateItemWithoutAvailable() {
        item.setAvailable(null);
        ValidationException ex = Assertions.assertThrows(
                ValidationException.class,
                () ->
                        itemService.getValidator().validateItem(item));
        Assertions.assertEquals("Ошибка валидации",
                ex.getMessage());
    }

    @Test
    void testUpdateItemWithUserNotOwner() {
        when(userRepository.getReferenceById(Mockito.anyLong()))
                .thenReturn(user);
        when(itemRepository.getReferenceById(Mockito.anyLong()))
                .thenReturn(item);
        UserIsNotOwnerException ex = Assertions.assertThrows(
                UserIsNotOwnerException.class,
                () ->
                        itemService.update(1L, 99L, itemDto));
        Assertions.assertEquals("Пользователь не владелец предмета",
                ex.getMessage());
    }

    @Test
    void testCreateCommentWithEmptyText() {
        commentDto.setText("");
        ValidationException ex = Assertions.assertThrows(
                ValidationException.class,
                () ->
                        itemService.createComment(1L, 99L, commentDto));
        Assertions.assertEquals("Комментарий не может быть пустым",
                ex.getMessage());
    }

    @Test
    void testCreateCommentWithUserNotBooker() {
        when(userRepository.getReferenceById(Mockito.anyLong()))
                .thenReturn(user);
        when(itemRepository.getReferenceById(Mockito.anyLong()))
                .thenReturn(item);
        when(bookingRepository.findByBooker_IdAndItem_Id(Mockito.anyLong(), Mockito.anyLong()))
                .thenReturn(Collections.emptyList());
        CommentCreateException ex = Assertions.assertThrows(
                CommentCreateException.class,
                () ->
                        itemService.createComment(1L, 99L, commentDto));
        Assertions.assertEquals("Пользователь не брал предмет в аренду",
                ex.getMessage());
    }

    @Test
    void testCreateCommentWithUserNotFinishedBooking() {
        Booking booking = new Booking();
        booking.setEnd(LocalDateTime.now().plusDays(1));
        when(userRepository.getReferenceById(Mockito.anyLong()))
                .thenReturn(user);
        when(itemRepository.getReferenceById(Mockito.anyLong()))
                .thenReturn(item);
        when(bookingRepository.findByBooker_IdAndItem_Id(Mockito.anyLong(), Mockito.anyLong()))
                .thenReturn(List.of(booking));
        CommentCreateException ex = Assertions.assertThrows(
                CommentCreateException.class,
                () ->
                        itemService.createComment(1L, 99L, commentDto));
        Assertions.assertEquals("Пользователь не закончил ни одной аренды предмета",
                ex.getMessage());
    }

}