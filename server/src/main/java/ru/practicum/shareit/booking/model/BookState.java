package ru.practicum.shareit.booking.model;

public enum BookState {
    ALL,
    CURRENT, //действующие
    PAST, //закрытые
    FUTURE, //зарезервированные
    WAITING, //ожидают реакции владельца
    REJECTED //получен отказ
}
