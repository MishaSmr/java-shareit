package ru.practicum.shareit.item;

import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemExtDto;

import java.util.Collection;

public interface ItemService {

    ItemDto create(Long userId, ItemDto itemDto);

    ItemDto update(Long itemId, Long userId, ItemDto itemDto);

    ItemExtDto getItem(Long itemId, Long userId);

    Collection<ItemExtDto> getAllForUser(Long userId, Integer from, Integer size);

    Collection<ItemDto> search(String text, Integer from, Integer size);

    CommentDto createComment(Long itemId, Long userId, CommentDto commentDto);
}
