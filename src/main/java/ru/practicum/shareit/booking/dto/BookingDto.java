package ru.practicum.shareit.booking.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import ru.practicum.shareit.booking.BookStatus;

import java.time.LocalDateTime;

/**
 * TODO Sprint add-bookings.
 */
@Data
@NoArgsConstructor
public class BookingDto {

    private Long id;                    // уникальный идентификатор бронирования;
    private LocalDateTime start;        // дата и время начала бронирования;
    private LocalDateTime end;          // дата и время конца бронирования;
    private Long item;                  // вещь, которую пользователь бронирует;
    private Long booker;                // пользователь, который осуществляет бронирование;
    private BookStatus status;          // статус бронирования

}
