package ru.practicum.shareit.user;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import ru.practicum.shareit.exceptions.ValidationException;
import ru.practicum.shareit.user.dto.UserDto;


@SpringBootTest
@RequiredArgsConstructor(onConstructor_ = @Autowired)
class UserServiceImplTest {

    private final UserServiceImpl userService;

    UserDto user = new UserDto(11L, "name", "1@1.com");

    @Test
    void testCreateUserWithEmptyName() {
        user.setName("");
        ValidationException ex = Assertions.assertThrows(
                ValidationException.class,
                () ->
                        userService.create(user));
        Assertions.assertEquals("Ошибка валидации",
                ex.getMessage());
    }

    @Test
    void testCreateUserWithEmptyEmail() {
        user.setEmail("");
        ValidationException ex = Assertions.assertThrows(
                ValidationException.class,
                () ->
                        userService.create(user));
        Assertions.assertEquals("Ошибка валидации",
                ex.getMessage());
    }

    @Test
    void testCreateUserWithWrongEmail() {
        user.setEmail("email");
        ValidationException ex = Assertions.assertThrows(
                ValidationException.class,
                () ->
                        userService.create(user));
        Assertions.assertEquals("Ошибка валидации",
                ex.getMessage());
    }
}