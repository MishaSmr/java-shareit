package ru.practicum.shareit.exceptions;

public class IncorrectParameterException extends RuntimeException {

    private final String parameter;
    private final String value;

    public IncorrectParameterException(String parameter, String value) {
        this.parameter = parameter;
        this.value = value;
    }

    public String getParameter() {
        return parameter;
    }

    public String getValue() {
        return value;
    }
}