package ru.practicum.shareit.auxiliary.exceptions;

public class NotFoundException extends ValidationException {
    public NotFoundException(String message, Object objForBody) {
        super(message, objForBody);
    }

}