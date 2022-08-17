package ru.practicum.shareit.requests;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.practicum.shareit.exceptions.ValidationException;

@Component
@Slf4j
public class ItemRequestValidator {

    public void validateItemRequest(ItemRequest itemRequest) throws ValidationException {
        if (itemRequest.getDescription() == null || itemRequest.getDescription().isEmpty()) {
            log.warn("Описание не может быть пустым");
            throw new ValidationException("Ошибка валидации");
        }
    }
}
