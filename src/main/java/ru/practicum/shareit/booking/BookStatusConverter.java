package ru.practicum.shareit.booking;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;
import ru.practicum.shareit.booking.model.BookStatus;

@Converter(autoApply = true)
public class BookStatusConverter implements AttributeConverter<BookStatus, String> {
    @Override
    public String convertToDatabaseColumn(BookStatus status) {
        return status != null ? status.name().toUpperCase() : null;
    }

    @Override
    public BookStatus convertToEntityAttribute(String dbStatus) {
        if (dbStatus == null) return null;
        return BookStatus.valueOf(dbStatus.toUpperCase()); // Приведение к верхнему регистру
    }
}