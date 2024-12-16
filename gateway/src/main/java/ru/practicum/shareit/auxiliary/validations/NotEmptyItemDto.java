package ru.practicum.shareit.auxiliary.validations;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.*;

//Аннотация для проверки полей запроса на update Item - каждое из полей может быть пустым, но не все три одновременно
//За проверку отвечает ValidateItemDtoForUpdate
@Documented
@Constraint(validatedBy = ValidateItemDtoForUpdate.class)
@Target(value = {ElementType.METHOD, ElementType.FIELD, ElementType.TYPE, ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
public @interface NotEmptyItemDto {

    String message() default "At least one of (name, description and available) must not be empty";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
