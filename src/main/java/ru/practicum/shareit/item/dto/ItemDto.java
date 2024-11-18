package ru.practicum.shareit.item.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.practicum.shareit.validations.OnCreate;

/**
 * TODO Sprint add-controllers.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ItemDto {

    private Long id; //— уникальный идентификатор вещи;

    @NotBlank(groups = OnCreate.class)
    private String name; //— краткое название;

    @NotBlank(groups = OnCreate.class)
    private String description; //— развёрнутое описание;

    @NotNull(groups = OnCreate.class)
    private Boolean available; //— статус о том, доступна или нет вещь для аренды;

    private Long owner; //— владелец вещи;

    private Long request; //— если вещь была создана по запросу другого пользователя,
    // то в этом поле будет храниться ссылка на соответствующий запрос.
}