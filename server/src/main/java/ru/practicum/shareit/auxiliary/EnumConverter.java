package ru.practicum.shareit.auxiliary;

//класс для использования в мапперах. поскольку уже есть 2 Enum, а могут появиться и еще, сделал для общего случая
public class EnumConverter {

    public <T extends Enum<T>> T fromString(String convertedValue, Class<T> enumClass) {
        return Enum.valueOf(enumClass, convertedValue.toUpperCase());
    }

    public String toString(Enum<?> convertedValue) {
        return convertedValue.name();
    }
}
