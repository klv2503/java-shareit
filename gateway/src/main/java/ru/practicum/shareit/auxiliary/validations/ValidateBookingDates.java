package ru.practicum.shareit.auxiliary.validations;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import ru.practicum.shareit.booking.dto.BookItemRequestDto;

import java.time.LocalDateTime;

public class ValidateBookingDates implements ConstraintValidator<CheckBookingDates, BookItemRequestDto> {

    @Override
    public boolean isValid(BookItemRequestDto value, ConstraintValidatorContext context) {
        if (value == null)
            return false;

        LocalDateTime firstDate = value.getStart();
        LocalDateTime secondDate = value.getEnd();

        //обе даты должны быть не null
        if (firstDate == null || secondDate == null)
            return false;

        //вторая дата должна быть позже первой
        if (!secondDate.isAfter(firstDate))
            return false;

        //обе даты должны быть в будущем
        return firstDate.isAfter(LocalDateTime.now());

    }

}
