package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.practicum.shareit.exceptions.ItemNotFoundException;

import java.util.Collection;
import java.util.HashMap;

@Component("inMemoryItemRepository")
@Slf4j
@RequiredArgsConstructor
public class InMemoryItemRepository {

    private final HashMap<Long, Item> items = new HashMap<>();
    private long id = 1;
    private final ItemValidator validator;

    public Item get(Long id) {
        if (!items.containsKey(id)) {
            log.warn("Предмет c таким id не найден.");
            throw new ItemNotFoundException("Предмет c таким id не найден.");
        }
        return items.get(id);
    }

    public Collection<Item> getAll() {
        return items.values();
    }

    public Item create(Item item) {
        validator.validateItem(item);
        item.setId(id++);
        items.put(item.getId(), item);
        log.debug("Текущее количество предметов: {}", items.size());
        return item;
    }

    public void remove(Item item) {
        if (!items.containsKey(id)) {
            log.warn("Предмет c таким id не найден.");
            throw new ItemNotFoundException("Предмет c таким id не найден.");
        }
        items.remove(item.getId());
    }

    public Item update(Item item) {
        validator.validateItem(item);
        items.put(item.getId(), item);
        log.debug("Текущее количество предметов: {}", items.size());
        return item;
    }
}
