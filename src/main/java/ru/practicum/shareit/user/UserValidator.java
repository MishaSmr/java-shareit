package ru.practicum.shareit.user;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.practicum.shareit.exceptions.ValidationException;

@Component
@Slf4j
public class UserValidator {
    public void validateUser(User user) throws ValidationException {
        if (user.getEmail() == null || user.getEmail().isEmpty()) {
            log.warn("e-mail не может быть пустым");
            throw new ValidationException("Ошибка валидации");
        }
        if (!user.getEmail().contains("@")) {
            log.warn("e-mail должен содержать знак @");
            throw new ValidationException("Ошибка валидации");
        }
        if (user.getName() == null || user.getName().isEmpty()) {
            log.warn("Имя не может быть пустым");
            throw new ValidationException("Ошибка валидации");
        }
    }
}
