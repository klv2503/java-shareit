package ru.practicum.shareit.auxiliary.validations;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

import java.util.Arrays;

public class ValidateEnumVariable implements ConstraintValidator<EnumValid, String> {

    private Class<? extends Enum<?>> classToValidation;

    @Override
    public void initialize(EnumValid appliedAnnotation) {
        this.classToValidation = appliedAnnotation.enumClass();
    }

    @Override
    public boolean isValid(String checkedValue, ConstraintValidatorContext constraintValidatorContext) {
        return (checkedValue == null) || Arrays.stream(classToValidation.getEnumConstants())
                .anyMatch(element -> element.name().equals(checkedValue.toUpperCase()));
    }
}
