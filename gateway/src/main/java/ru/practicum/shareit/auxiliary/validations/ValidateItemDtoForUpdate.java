package ru.practicum.shareit.auxiliary.validations;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import org.apache.logging.log4j.util.Strings;
import ru.practicum.shareit.item.dto.ItemDto;

public class ValidateItemDtoForUpdate implements ConstraintValidator<NotEmptyItemDto, ItemDto> {
    @Override
    public boolean isValid(ItemDto itemDto, ConstraintValidatorContext constraintValidatorContext) {
        return !Strings.isBlank(itemDto.getName()) ||
                !Strings.isBlank(itemDto.getDescription()) ||
                itemDto.getAvailable() != null;
    }
}
