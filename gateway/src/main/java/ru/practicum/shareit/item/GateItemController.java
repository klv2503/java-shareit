package ru.practicum.shareit.item;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.auxiliary.validations.NotEmptyItemDto;
import ru.practicum.shareit.auxiliary.validations.OnCreate;
import ru.practicum.shareit.item.dto.CommentInputDto;
import ru.practicum.shareit.item.dto.ItemDto;

@RestController
@RequestMapping(path = "/items")
@RequiredArgsConstructor
@Slf4j
@Validated
public class GateItemController {

    private final ItemClient itemClient;

    @PostMapping
    public ResponseEntity<Object> createItem(@RequestHeader("X-Sharer-User-Id") @NotNull @Positive Long id,
                                             @Validated(OnCreate.class) @Valid @RequestBody ItemDto itemDto) {
        log.info("\nGateway: Получен запрос на добавление item {} от user {}", itemDto, id);
        itemDto.setOwner(id);
        return itemClient.createItem(id, itemDto);
    }

    @PatchMapping("/{itemId}")
    public ResponseEntity<Object> updateItem(@RequestHeader("X-Sharer-User-Id") @NotNull @Positive Long owner,
                                             @PathVariable @NotNull @Positive Long itemId,
                                             @RequestBody @Valid @NotEmptyItemDto ItemDto itemDto) {
        log.info("\nGateway: Получен запрос на изменение item {} данных на {} от user {}", itemId, itemDto, owner);
        return itemClient.updateItem(owner, itemId, itemDto);
    }

    @DeleteMapping("/{itemId}")
    public ResponseEntity<Object> deleteItem(@RequestHeader("X-Sharer-User-Id") @NotNull @Positive Long id,
                                             @PathVariable @NotNull @Positive Long itemId) {
        log.info("\nGateway: Получен запрос на удаление item {} от user {}", itemId, id);
        return itemClient.deleteItem(id, itemId);
    }

    @GetMapping("/{itemId}")
    public ResponseEntity<Object> getItemById(@PathVariable @NotNull @Positive Long itemId) {
        log.info("\nПолучен запрос на просмотр данных item {}", itemId);
        return itemClient.getItem(itemId);
    }

    @GetMapping
    public ResponseEntity<Object> getAllItemsOfOwner(@RequestHeader("X-Sharer-User-Id") @NotNull @Positive Long id) {
        log.info("\nGateway: Получен запрос на получение всех items пользователя {}", id);
        return itemClient.getItems(id);
    }

    ///items/search?text={text}
    @GetMapping("/search")
    public ResponseEntity<Object> getItemsByContext(@RequestParam(name = "text") String query) {
        log.info("\nGateway: Получен запрос на получение всех items по контексту ...{}...", query);
        return itemClient.getItems(query);
    }

    //POST /items/{itemId}/comment
    @PostMapping("/{itemId}/comment")
    public ResponseEntity<Object> addNewComment(@RequestHeader("X-Sharer-User-Id") @NotNull @Positive Long id,
                                                @PathVariable @NotNull @Positive Long itemId,
                                                @RequestBody @Valid CommentInputDto newComment) {
        log.info("\nGateway: Получен запрос на добавление комментария от пользователя {} по item {} textsize {}",
                id, itemId, newComment.getText().length());
        return itemClient.addNewComment(id, itemId, newComment);
    }

}
