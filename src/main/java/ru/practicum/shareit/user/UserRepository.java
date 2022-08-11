package ru.practicum.shareit.user;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.practicum.shareit.exceptions.UserNotFoundException;

import javax.persistence.EntityNotFoundException;

public interface UserRepository extends JpaRepository<User, Long> {



    default boolean checkUserId(Long userId) {
        try {
            User user = getReferenceById(userId);
            UserMapper.toUserDto(user);
        } catch (EntityNotFoundException ex) {
            throw new UserNotFoundException("Пользователь c таким id не найден.");
        }
        return true;
    }
}
