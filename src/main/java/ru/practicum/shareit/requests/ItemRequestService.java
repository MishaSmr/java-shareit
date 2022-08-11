package ru.practicum.shareit.requests;

import ru.practicum.shareit.requests.dto.ItemRequestDto;
import ru.practicum.shareit.requests.dto.ItemRequestExtDto;

import java.util.Collection;

public interface ItemRequestService {

    ItemRequestDto create(Long userId, ItemRequestDto itemRequestDto);

    Collection<ItemRequestExtDto> getForUser(Long userId);

    Collection<ItemRequestExtDto> getAll(Long userId, Integer from, Integer size);

    ItemRequestExtDto getById(Long userId, Long requestId);

}
