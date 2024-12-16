package ru.practicum.shareit.booking.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.practicum.shareit.booking.model.BookStatus;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.User;

import java.time.LocalDateTime;

/**
 * TODO Sprint add-bookings.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class BookingOutputDto {

    private Long id;                    // уникальный идентификатор бронирования;

    private LocalDateTime start;        // дата и время начала бронирования;

    private LocalDateTime end;          // дата и время конца бронирования;

    private Item item;                  // вещь, которую пользователь бронирует;

    private User booker;                // пользователь, который осуществляет бронирование;

    private BookStatus status;          // статус бронирования

}
