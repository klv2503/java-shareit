package ru.practicum.shareit.item.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
//Класс для вывода временных данных о бронированиях
public class PairOfDate {

    private LocalDateTime startDateTime;

    private LocalDateTime endDateTime;
}
