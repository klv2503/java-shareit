package ru.practicum.shareit.request.dto;

import jakarta.validation.constraints.Size;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * TODO Sprint add-item-requests.
 */
@Data
@NoArgsConstructor
public class ItemRequestDto {

    private Long id; // уникальный идентификатор запроса;

    @Size(max = 200)
    private String description; // текст запроса, содержащий описание требуемой вещи;

    private Long requestor; // пользователь, создавший запрос;

    private LocalDateTime created; // дата и время создания запроса

}
