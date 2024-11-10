package ru.practicum.shareit.exceptions;

public class DuplicateDataException extends ValidationException {
    public DuplicateDataException(String message, Object objForBody) {
        super(message, objForBody);
    }
}
