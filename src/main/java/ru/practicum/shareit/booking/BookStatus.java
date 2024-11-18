package ru.practicum.shareit.booking;

public enum BookStatus {

    WAITING,  // новое бронирование, ожидает одобрения,
    APPROVED, // бронирование подтверждено владельцем,
    REJECTED, // бронирование отклонено владельцем,
    CANCELED  // бронирование отменено создателем

}
