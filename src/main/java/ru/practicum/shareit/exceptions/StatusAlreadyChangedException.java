package ru.practicum.shareit.exceptions;

public class StatusAlreadyChangedException extends RuntimeException {
    public StatusAlreadyChangedException(String message) {
        super(message);
    }
}
