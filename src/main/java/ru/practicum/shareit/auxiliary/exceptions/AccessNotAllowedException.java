package ru.practicum.shareit.auxiliary.exceptions;

public class AccessNotAllowedException extends ValidationException {

    public AccessNotAllowedException(String message, Object objForBody) {
        super(message, objForBody);
    }
}
