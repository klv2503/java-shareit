package ru.practicum.shareit.item.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import ru.practicum.shareit.item.model.PairOfDate;
import ru.practicum.shareit.request.ItemRequest;
import ru.practicum.shareit.user.User;

import java.util.List;

@Data
@NoArgsConstructor
public class ItemWithCommentsDto {

    private Long id;

    private String name;

    private String description;

    private Boolean available;

    private User owner;

    private ItemRequest request;

    private PairOfDate lastBooking;

    private PairOfDate nextBooking;

    private List<ShortCommentDto> comments;
}
