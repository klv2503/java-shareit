package ru.practicum.shareit.item.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import ru.practicum.shareit.request.ItemRequest;
import ru.practicum.shareit.user.User;

@Data
@NoArgsConstructor
public class ItemOutputDto {
    private Long id; //— уникальный идентификатор вещи;

    private String name; //— краткое название;

    private String description; //— развёрнутое описание;

    private Boolean available; //— статус о том, доступна или нет вещь для аренды;

    private User owner; //— владелец вещи;

    private ItemRequest request;
}
