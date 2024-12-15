package ru.practicum.shareit.item.dto.items;

import lombok.Data;
import lombok.NoArgsConstructor;
import ru.practicum.shareit.item.dto.comments.ShortCommentDto;
import ru.practicum.shareit.item.model.PairOfDate;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.user.User;

import java.util.List;

@Data
@NoArgsConstructor
public class ItemOutputDto {
    private Long id; //— уникальный идентификатор вещи;

    private String name; //— краткое название;

    private String description; //— развёрнутое описание;

    private Boolean available; //— статус о том, доступна или нет вещь для аренды;

    private User owner; //— владелец вещи;

    private ItemRequest request;

    private PairOfDate lastBooking;

    private PairOfDate nextBooking;

    private List<ShortCommentDto> comments;

}
