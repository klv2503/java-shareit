package ru.practicum.shareit.item.dto.items;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * TODO Sprint add-controllers.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ItemDto {

    private Long id; //— уникальный идентификатор вещи;

    private String name; //— краткое название;

    private String description; //— развёрнутое описание;

    private Boolean available; //— статус о том, доступна или нет вещь для аренды;

    private Long owner; //— владелец вещи;

    private Long requestId; //— если вещь была создана по запросу другого пользователя,
    // то в этом поле будет храниться ссылка на соответствующий запрос.
}