package ru.practicum.shareit.user;

import java.util.Collection;

public interface UserRepository {

    User get(Long id);

    Collection<User> getAll();

    User create(User user);

    void remove(long id);

    User update(User user);
}
