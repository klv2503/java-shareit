package ru.practicum.shareit.auxiliary.exceptions;

import lombok.Getter;

@Getter
public class ValidationException extends RuntimeException {
    Object objForBody;

    public ValidationException(String message, Object objForBody) {
        super(message);
        this.objForBody = objForBody;
    }

}