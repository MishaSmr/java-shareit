package ru.practicum.shareit.requests;

import ru.practicum.shareit.requests.dto.ItemRequestDto;
import ru.practicum.shareit.user.UserRepository;

public class ItemRequestMapper {

    private static UserRepository userRepository;

    public static ItemRequestDto toItemRequestDto (ItemRequest itemRequest) {
        return new ItemRequestDto(
           itemRequest.getId(),
           itemRequest.getDescription(),
           itemRequest.getRequester().getId(),
           itemRequest.getCreated()
        );
    }
}
