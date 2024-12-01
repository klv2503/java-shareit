package ru.practicum.shareit.item.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
//Класс используется только для выдачи результата
public class CommentDto {

    private Long id;

    private String text;

    private String itemName;

    private String authorName;

    private LocalDateTime created;

}
