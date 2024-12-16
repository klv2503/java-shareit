package ru.practicum.shareit.auxiliary.exceptions;

public class DuplicateDataException extends ValidationException {
    public DuplicateDataException(String message, Object objForBody) {
        super(message, objForBody);
    }
}
