package ru.practicum.shareit.item.dto.items;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ShortItemDto {

    private Long itemId;

    private String name;

    private Long ownerId;
}
