package ru.practicum.shareit.requests;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import ru.practicum.shareit.exceptions.UserNotDefinedException;
import ru.practicum.shareit.exceptions.UserNotFoundException;
import ru.practicum.shareit.exceptions.ValidationException;
import ru.practicum.shareit.requests.dto.ItemRequestDto;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserRepository;

import java.time.LocalDateTime;

import static org.mockito.Mockito.when;

@SpringBootTest
@RequiredArgsConstructor(onConstructor_ = @Autowired)
class ItemRequestServiceImplTest {

    private final ItemRequestServiceImpl itemRequestService;

    @MockBean
    UserRepository userRepository;

    ItemRequestDto itemRequestDto = new ItemRequestDto(
            1L,
            "Описание",
            11L,
            LocalDateTime.now()
    );

    @Test
    void testCreateItemRequestWithUserIdIsNull() {
        UserNotDefinedException ex = Assertions.assertThrows(
                UserNotDefinedException.class,
                () ->
                        itemRequestService.create(null, itemRequestDto));
        Assertions.assertEquals("Пользователь не определен",
                ex.getMessage());
    }

    @Test
    void testCreateItemRequestWithUserNotFound() {
        when(userRepository.getReferenceById(Mockito.anyLong()))
                .thenThrow(new UserNotFoundException("Пользователь c таким id не найден."));
        UserNotFoundException ex = Assertions.assertThrows(
                UserNotFoundException.class,
                () ->
                        itemRequestService.create(11L, itemRequestDto));
        Assertions.assertEquals("Пользователь c таким id не найден.",
                ex.getMessage());
    }

    @Test
    void testCreateItemRequestWithEmptyDescription() {
        when(userRepository.getReferenceById(Mockito.anyLong()))
                .thenReturn(new User(11L, "name", "1@1.com"));
        itemRequestDto.setDescription("");
        ValidationException ex = Assertions.assertThrows(
                ValidationException.class,
                () ->
                        itemRequestService.create(11L, itemRequestDto));
        Assertions.assertEquals("Ошибка валидации",
                ex.getMessage());
    }
}