package ru.practicum.shareit.booking.dto;

//В обновленной модели можно было обойтись без Enum (и его валидации), т.к. входит значение типа String и
//gateway отдает его, не изменяя, в server (т.е. можно сделать проверку строки), но пока решил оставить
public enum BookState {
    // Все
    ALL,
    // Текущие
    CURRENT,
    // Будущие
    FUTURE,
    // Завершенные
    PAST,
    // Отклоненные
    REJECTED,
    // Ожидающие подтверждения
    WAITING;

}