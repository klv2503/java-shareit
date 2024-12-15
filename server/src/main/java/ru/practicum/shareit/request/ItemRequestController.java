package ru.practicum.shareit.request;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.request.dto.RequestDto;
import ru.practicum.shareit.request.service.RequestService;

import java.util.List;

/**
 * TODO Sprint add-item-requests.
 */
@RestController
@RequestMapping(path = "/requests")
@RequiredArgsConstructor
@Slf4j
public class ItemRequestController {

    private final RequestService requestService;

    @PostMapping
    //POST /requests — добавить новый запрос вещи.
    //Основная часть запроса — текст запроса, в котором пользователь описывает, какая именно вещь ему нужна.
    public ResponseEntity<RequestDto> addNewRequest(@RequestHeader("X-Sharer-User-Id") Long id,
                                                    @RequestBody RequestDto requestDto) {
        log.info("\nServer: Получен запрос на поиск item {}", requestDto);
        return ResponseEntity.ok(requestService.addNewRequest(id, requestDto));
    }

    @GetMapping
    //GET /requests — получить список своих запросов вместе с данными об ответах на них.
    public ResponseEntity<List<RequestDto>> getUsersRequests(@RequestHeader("X-Sharer-User-Id") Long id) {
        log.info("\nServer: Получен запрос пользователя {} на поиск его запросов", id);
        return ResponseEntity.ok(requestService.getUsersRequests(id));
    }

    @GetMapping("/all")
    //GET /requests/all — получить список запросов, созданных другими пользователями.
    //С помощью этого эндпоинта пользователи смогут просматривать существующие запросы, на которые они
    //могли бы ответить. Запросы сортируются по дате создания от более новых к более старым.
    public ResponseEntity<List<RequestDto>> getAllRequests(@RequestHeader("X-Sharer-User-Id") Long id) {
        log.info("\nServer: Получен запрос пользователя {} на поиск чужих запросов", id);
        return ResponseEntity.ok(requestService.getAllRequests(id));
    }

    @GetMapping("/{requestId}")
    //GET /requests/{requestId} — получить данные об одном конкретном запросе вместе с данными об ответах на него
    // в том же формате, что и в эндпоинте GET /requests. Посмотреть данные об отдельном запросе может
    // любой пользователь
    public ResponseEntity<RequestDto> getRequestById(@RequestHeader("X-Sharer-User-Id") Long id,
                                                     @PathVariable(name = "requestId") Long requestId) {
        log.info("\nServer: Получен запрос пользователя {} на просмотр запроса {}", id, requestId);
        return ResponseEntity.ok(requestService.getRequestById(id, requestId));
    }
}
