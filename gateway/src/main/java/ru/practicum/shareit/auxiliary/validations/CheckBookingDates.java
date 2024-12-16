package ru.practicum.shareit.auxiliary.validations;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.*;

@Documented
@Constraint(validatedBy = ValidateBookingDates.class)
@Target(value = {ElementType.TYPE, ElementType.FIELD, ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
public @interface CheckBookingDates {

    String message() default "End date must be later than start date. Both must be not null and in future.";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};

}
