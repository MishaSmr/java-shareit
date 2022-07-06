package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exceptions.UserIsNotOwnerException;
import ru.practicum.shareit.exceptions.UserNotDefinedException;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.user.InMemoryUserRepository;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
public class ItemServiceImpl implements ItemService {

    private final InMemoryItemRepository itemRepository;
    private final InMemoryUserRepository userRepository;

    @Override
    public ItemDto create(Long userId, ItemDto itemDto) {
        isUserDefined(userId);
        Item item = ItemMapper.toItem(itemDto);
        item.setOwner(userRepository.get(userId));
        return ItemMapper.toItemDto(itemRepository.create(item));
    }

    @Override
    public ItemDto update(Long itemId, Long userId, ItemDto itemDto) {
        isUserDefined(userId);
        Item item = itemRepository.get(itemId);
        if (item.getOwner().getId() != userId) {
            log.warn("Пользователь не владелец предмета");
            throw new UserIsNotOwnerException("Пользователь не владелец предмета");
        }
        if (itemDto.getName() != null) item.setName(itemDto.getName());
        if (itemDto.getDescription() != null) item.setDescription(itemDto.getDescription());
        if (itemDto.getAvailable() != null) item.setAvailable(itemDto.getAvailable());
        return ItemMapper.toItemDto(itemRepository.update(item));
    }

    @Override
    public ItemDto getItem(Long itemId) {
        return ItemMapper.toItemDto(itemRepository.get(itemId));
    }

    @Override
    public Collection<ItemDto> getAllForUser(Long userId) {
        isUserDefined(userId);
        return itemRepository.getAll().stream().
                filter(i -> i.getOwner().getId() == userId).
                map(ItemMapper::toItemDto).
                collect(Collectors.toList());
    }

    @Override
    public Collection<ItemDto> search(String text) {
        if (text.isEmpty()) {
            return Collections.emptyList();
        }
        String t = text.toLowerCase();
        return itemRepository.getAll().stream().
                filter(i -> i.getDescription().toLowerCase().contains(t) ||
                        i.getName().toLowerCase().contains(t)).
                filter(Item::getAvailable).
                map(ItemMapper::toItemDto).
                collect(Collectors.toList());
    }

    private void isUserDefined(Long userId) {
        if (userId == null) {
            log.warn("Пользователь не определен");
            throw new UserNotDefinedException("Пользователь не определен");
        }
    }
}
