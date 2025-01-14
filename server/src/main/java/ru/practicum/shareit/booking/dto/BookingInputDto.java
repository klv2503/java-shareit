package ru.practicum.shareit.booking.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BookingInputDto {

    private Long id; // уникальный идентификатор бронирования  - пока не используется, оставил на случай update;

    private LocalDateTime start;

    private LocalDateTime end;

    private Long itemId;

    private Long booker;

    private String status;
}
