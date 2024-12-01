package ru.practicum.shareit.booking.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.practicum.shareit.auxiliary.validations.CheckBookingDates;
import ru.practicum.shareit.auxiliary.validations.EnumValid;
import ru.practicum.shareit.auxiliary.validations.OnCreate;
import ru.practicum.shareit.booking.model.BookStatus;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@CheckBookingDates(groups = OnCreate.class)
public class BookingInputDto {

    private Long id; // уникальный идентификатор бронирования  - пока не используется, оставил на случай update;

    //Валидация дат (в т.ч. не null) проводится согласно аннотации @CheckBookingDates
    private LocalDateTime start;

    private LocalDateTime end;

    @NotNull(groups = OnCreate.class)
    @Positive(groups = OnCreate.class)
    private Long itemId;

    private Long booker;

    @EnumValid(enumClass = BookStatus.class)
    private String status;
}
