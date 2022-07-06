package ru.practicum.shareit.item;

import java.util.Collection;

public interface ItemRepository {
    Item get(Long id);

    Collection<Item> getAll();

    Item create(Item item);

    void remove(Item item);

    Item update(Item item);
}
