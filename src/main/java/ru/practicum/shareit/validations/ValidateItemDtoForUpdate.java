package ru.practicum.shareit.validations;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import ru.practicum.shareit.item.dto.ItemDto;

public class ValidateItemDtoForUpdate implements ConstraintValidator<NotEmptyItemDto, ItemDto> {
    @Override
    public boolean isValid(ItemDto itemDto, ConstraintValidatorContext constraintValidatorContext) {
        return itemDto != null && (
                itemDto.getName() != null && !itemDto.getName().isBlank() ||
                        itemDto.getDescription() != null && !itemDto.getDescription().isBlank() ||
                        itemDto.getAvailable() != null
        );
    }
}
