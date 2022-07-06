package ru.practicum.shareit.requests;

import ru.practicum.shareit.user.User;

import java.util.Collection;

public interface ItemRequestRepository {

    ItemRequest get(Long id);

    Collection<ItemRequest> getAll();

    ItemRequest create(ItemRequest itemRequest);

    void remove(long id);

    ItemRequest update(ItemRequest itemRequest);
}
