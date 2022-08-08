package ru.practicum.shareit.item;

import lombok.Data;
import lombok.RequiredArgsConstructor;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemExtDto;
import ru.practicum.shareit.requests.ItemRequestRepository;

import java.util.Collections;

@Data
@RequiredArgsConstructor
public class ItemMapper {
    private static ItemRequestRepository itemRequestRepository;

    public static ItemDto toItemDto(Item item) {
        return new ItemDto(
                item.getId(),
                item.getName(),
                item.getDescription(),
                item.getAvailable(),
                item.getRequest() != null ? item.getRequest().getId() : null
        );
    }

    public static Item toItem(ItemDto itemDto) {
        return new Item(
                itemDto.getId(),
                itemDto.getName(),
                itemDto.getDescription(),
                itemDto.getAvailable(),
                null,
                itemDto.getRequestId() != null ? itemRequestRepository.get(itemDto.getRequestId()) : null
                );
    }

    public static ItemExtDto toItemExtDto(Item item) {
        return new ItemExtDto(
                item.getId(),
                item.getName(),
                item.getDescription(),
                item.getAvailable(),
                item.getRequest() != null ? item.getRequest().getId() : null,
                null,
                null,
                Collections.emptyList()
        );
    }
}
