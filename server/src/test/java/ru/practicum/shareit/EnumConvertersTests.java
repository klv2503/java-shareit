package ru.practicum.shareit;

import org.junit.jupiter.api.Test;
import ru.practicum.shareit.auxiliary.EnumConverter;
import ru.practicum.shareit.booking.BookStatusConverter;
import ru.practicum.shareit.booking.model.BookState;
import ru.practicum.shareit.booking.model.BookStatus;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class EnumConvertersTests {

    @Test
    public void shouldCorrectConvertStringsToBookState() {
        EnumConverter converter = new EnumConverter();
        BookState[] statesPattern = {BookState.ALL, BookState.CURRENT, BookState.FUTURE,
                        BookState.PAST, BookState.WAITING, BookState.REJECTED};
        String[] strings = {"All", "CUrrENT", "fuTURE", "PASt", "WAITinG", "rejected"};
        BookState state;
        for (int i = 0; i < 6; i++) {
            state = converter.fromString(strings[i], BookState.class);
            assertEquals(statesPattern[i], state);
        }
    }

    @Test
    public void shouldCorrectConvertBookStateToStrings() {
        EnumConverter converter = new EnumConverter();
        BookState[] statesPattern = {BookState.ALL, BookState.CURRENT, BookState.FUTURE,
                        BookState.PAST, BookState.WAITING, BookState.REJECTED};
        String[] strings = {"ALL", "CURRENT", "FUTURE", "PAST", "WAITING", "REJECTED"};
        String state;
        for (int i = 0; i < 6; i++) {
            state = converter.toString(statesPattern[i]);
            assertEquals(strings[i], state);
        }
    }

    @Test
    public void shouldCorrectConvertStringsToBookStatus() {
        EnumConverter converter = new EnumConverter();
        BookStatus[] statesPattern = {BookStatus.CANCELED, BookStatus.APPROVED, BookStatus.WAITING, BookStatus.REJECTED};
        String[] strings = {"caNCelED", "AppRoVeD", "WAITinG", "rejected"};
        BookStatus state;
        for (int i = 0; i < 4; i++) {
            state = converter.fromString(strings[i], BookStatus.class);
            assertEquals(statesPattern[i], state);
        }
    }

    @Test
    public void shouldCorrectConvertBookStatusToStrings() {
        EnumConverter converter = new EnumConverter();
        BookStatus[] statesPattern = {BookStatus.CANCELED, BookStatus.APPROVED, BookStatus.WAITING, BookStatus.REJECTED};
        String[] strings = {"CANCELED", "APPROVED", "WAITING", "REJECTED"};
        String state;
        for (int i = 0; i < 4; i++) {
            state = converter.toString(statesPattern[i]);
            assertEquals(strings[i], state);
        }
    }

    @Test
    public void shouldCorrectConvertStringsToEntityAttribute() {
        BookStatusConverter converter = new BookStatusConverter();
        BookStatus[] statesPattern = {BookStatus.CANCELED, BookStatus.APPROVED, BookStatus.WAITING, BookStatus.REJECTED};
        String[] strings = {"CANCELED", "APPROVED", "WAITING", "REJECTED"};
        BookStatus state;
        for (int i = 0; i < 4; i++) {
            state = converter.convertToEntityAttribute(strings[i]);
            assertEquals(statesPattern[i], state);
        }
    }

    @Test
    public void shouldCorrectConvertBookStatusToDBColumn() {
        BookStatusConverter converter = new BookStatusConverter();
        BookStatus[] statesPattern = {BookStatus.CANCELED, BookStatus.APPROVED, BookStatus.WAITING, BookStatus.REJECTED};
        String[] strings = {"CANCELED", "APPROVED", "WAITING", "REJECTED"};
        String state;
        for (int i = 0; i < 4; i++) {
            state = converter.convertToDatabaseColumn(statesPattern[i]);
            assertEquals(strings[i], state);
        }
    }


}
