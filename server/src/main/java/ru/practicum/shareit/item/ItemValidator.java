package ru.practicum.shareit.item;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.practicum.shareit.exceptions.ValidationException;

@Component
@Slf4j
public class ItemValidator {

    public void validateItem(Item item) throws ValidationException {
        if (item.getName() == null || item.getName().isEmpty()) {
            log.warn("Название не может быть пустым");
            throw new ValidationException("Ошибка валидации");
        }
        if (item.getDescription() == null || item.getDescription().isEmpty()) {
            log.warn("Описание не может быть пустым");
            throw new ValidationException("Ошибка валидации");
        }
        if (item.getAvailable() == null) {
            log.warn("Статус не может быть не указан");
            throw new ValidationException("Ошибка валидации");
        }
    }
}
