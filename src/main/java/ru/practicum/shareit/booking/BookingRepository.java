package ru.practicum.shareit.booking;

import java.util.Collection;

public interface BookingRepository {

    Booking get(Long id);

    Collection<Booking> getAll();

    Booking create(Booking booking);

    void remove(long id);

    Booking update(Booking booking);
}
