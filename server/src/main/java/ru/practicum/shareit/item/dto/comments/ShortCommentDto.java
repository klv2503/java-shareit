package ru.practicum.shareit.item.dto.comments;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ShortCommentDto {

    private Long id;

    private String text;

    private String authorName;

    private LocalDateTime created;
}
