package ru.practicum.shareit.exceptions;

public class UserNotDefinedException extends RuntimeException {
    public UserNotDefinedException(String message) {
        super(message);
    }
}
