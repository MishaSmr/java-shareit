package ru.practicum.shareit.user;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.practicum.shareit.exceptions.UserAlreadyExistsException;
import ru.practicum.shareit.exceptions.UserNotFoundException;

import java.util.Collection;
import java.util.HashMap;

@Component("inMemoryUserRepository")
@Slf4j
@RequiredArgsConstructor
public class InMemoryUserRepository {

    private final HashMap<Long, User> users = new HashMap<>();
    private long id = 1;

    private final UserValidator validator;

    public Collection<User> getAll() {
        return users.values();
    }

    public User get(Long id) {
        if (!users.containsKey(id)) {
            log.warn("Пользователь c таким id не найден.");
            throw new UserNotFoundException("Пользователь c таким id не найден.");
        }
        return users.get(id);
    }

    public User create(User user) {
        validator.validateUser(user);
        isEmailExists(user.getEmail());
        user.setId(id++);
        users.put(user.getId(), user);
        log.debug("Текущее количество пользователей: {}", users.size());
        return user;
    }

    public void remove(long id) {
        if (!users.containsKey(id)) {
            log.warn("Пользователь не найден.");
            throw new UserNotFoundException("Пользователь не найден.");
        }
        users.remove(id);
    }

    public User update(User user) {
        validator.validateUser(user);
        users.put(user.getId(), user);
        log.debug("Текущее количество пользователей: {}", users.size());
        return user;
    }

    public void isEmailExists(String email) {
        for (User u : users.values()) {
            if (u.getEmail().equals(email)) {
                log.warn("Пользователь с таким e-mail уже существует");
                throw new UserAlreadyExistsException("Пользователь с таким e-mail уже существует");
            }
        }
    }
}