package ru.practicum.shareit.request.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.practicum.shareit.item.dto.items.ShortItemDto;

import java.time.LocalDateTime;
import java.util.List;

/**
 * TODO Sprint add-item-requests.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RequestDto {

    private Long id; // уникальный идентификатор запроса;

    private String description; // текст запроса, содержащий описание требуемой вещи;

    private Long requestor; // пользователь, создавший запрос;

    private LocalDateTime created; // дата и время создания запроса

    private List<ShortItemDto> items;

}
