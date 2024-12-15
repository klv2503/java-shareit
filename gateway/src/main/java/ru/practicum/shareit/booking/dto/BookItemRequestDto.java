package ru.practicum.shareit.booking.dto;

import java.time.LocalDateTime;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.*;
import ru.practicum.shareit.auxiliary.validations.CheckBookingDates;
import ru.practicum.shareit.auxiliary.validations.OnCreate;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@CheckBookingDates(groups = OnCreate.class)
public class BookItemRequestDto {

    @NotNull(groups = OnCreate.class)
    @Positive(groups = OnCreate.class)
    private long itemId;

    //Валидация дат (в т.ч. не null) проводится согласно аннотации @CheckBookingDates
    private LocalDateTime start;

    private LocalDateTime end;
}