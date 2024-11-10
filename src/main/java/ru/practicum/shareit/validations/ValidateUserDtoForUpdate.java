package ru.practicum.shareit.validations;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import org.apache.logging.log4j.util.Strings;
import ru.practicum.shareit.user.dto.UserDto;

public class ValidateUserDtoForUpdate implements ConstraintValidator<NotEmptyUserDto, UserDto> {
    @Override
    public boolean isValid(UserDto userDto, ConstraintValidatorContext constraintValidatorContext) {
        return !Strings.isBlank(userDto.getName()) ||
                !Strings.isBlank(userDto.getEmail());
    }
}
