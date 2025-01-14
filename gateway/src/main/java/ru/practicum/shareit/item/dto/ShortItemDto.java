package ru.practicum.shareit.item.dto;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class ShortItemDto {

    private Long itemId;

    private String name;

    private Long ownerId;

}
