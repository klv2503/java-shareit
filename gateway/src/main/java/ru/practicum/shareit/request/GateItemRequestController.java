package ru.practicum.shareit.request;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.request.dto.RequestDto;

@RestController
@RequestMapping(path = "/requests")
@RequiredArgsConstructor
@Slf4j
@Validated
public class GateItemRequestController {

    private final ItemRequestClient requestClient;

    @PostMapping
    //POST /requests — добавить новый запрос вещи.
    //Основная часть запроса — текст запроса, в котором пользователь описывает, какая именно вещь ему нужна.
    public ResponseEntity<Object> addNewRequest(@RequestHeader("X-Sharer-User-Id") @NotNull @Positive Long id,
                                                @RequestBody @Valid RequestDto requestDto) {
        log.info("\nGateway: Получен запрос на поиск item {}", requestDto);
        return requestClient.addNewRequest(id, requestDto);
    }

    @GetMapping
    //GET /requests — получить список своих запросов вместе с данными об ответах на них.
    // Для каждого запроса должны быть указаны описание, дата и время создания, а также список
    //ответов в формате: id вещи, название, id владельца. В дальнейшем, используя указанные id вещей,
    // можно будет получить подробную информацию о каждой из них. Запросы должны возвращаться отсортированными от
    // более новых к более старым.
    public ResponseEntity<Object> getUsersRequests(@RequestHeader("X-Sharer-User-Id") @NotNull @Positive Long id) {
        log.info("\nGateway: Пользователь {} хочет получить список своих запросов", id);
        return requestClient.getUsersRequests(id);
    }

    @GetMapping("/all")
    //GET /requests/all — получить список запросов, созданных другими пользователями.
    //С помощью этого эндпоинта пользователи смогут просматривать существующие запросы, на которые они
    //могли бы ответить. Запросы сортируются по дате создания от более новых к более старым.
    public ResponseEntity<Object> getAllRequests(@RequestHeader("X-Sharer-User-Id") @NotNull @Positive Long id) {
        log.info("\nGateway: Пользователь {} хочет получить список чужих запросов", id);
        return requestClient.getAllRequests(id);
    }

    @GetMapping("/{requestId}")
    //GET /requests/{requestId} — получить данные об одном конкретном запросе вместе с данными об ответах на него
    // в том же формате, что и в эндпоинте GET /requests. Посмотреть данные об отдельном запросе может
    // любой пользователь
    public ResponseEntity<Object> getRequestById(@RequestHeader("X-Sharer-User-Id") @NotNull @Positive Long id,
                                                 @PathVariable(name = "requestId") @NotNull @Positive Long requestId) {
        log.info("\nGateway: Пользователь {} хочет получить информацию по запросу {}", id, requestId);
        return requestClient.getRequestById(id, requestId);
    }

}
