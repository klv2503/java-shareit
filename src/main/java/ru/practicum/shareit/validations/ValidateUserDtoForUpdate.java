package ru.practicum.shareit.validations;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import ru.practicum.shareit.user.dto.UserDto;

public class ValidateUserDtoForUpdate implements ConstraintValidator<NotEmptyUserDto, UserDto> {
    @Override
    public boolean isValid(UserDto userDto, ConstraintValidatorContext constraintValidatorContext) {
        return userDto != null && (
                userDto.getName() != null && !userDto.getName().isBlank() ||
                        userDto.getEmail() != null && !userDto.getEmail().isBlank()
        );
    }
}
