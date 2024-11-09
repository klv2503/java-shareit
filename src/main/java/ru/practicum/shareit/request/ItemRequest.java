package ru.practicum.shareit.request;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * TODO Sprint add-item-requests.
 */
@Data
@NoArgsConstructor
public class ItemRequest {

    private Long id; // уникальный идентификатор запроса;
    private String description; // текст запроса, содержащий описание требуемой вещи;
    private Long requestor; // пользователь, создавший запрос;
    private LocalDateTime created; // дата и время создания запроса

}