package ru.practicum.shareit.item;

import org.springframework.web.bind.annotation.PathVariable;
import ru.practicum.shareit.item.dto.ItemDto;

import java.util.Collection;

public interface ItemService {

    ItemDto create(Long userId, ItemDto itemDto);

    ItemDto update(Long itemId, Long userId, ItemDto itemDto);

    ItemDto getItem(@PathVariable Long itemId);

    Collection<ItemDto> getAllForUser(Long userId);

    Collection<ItemDto> search(String text);
}
