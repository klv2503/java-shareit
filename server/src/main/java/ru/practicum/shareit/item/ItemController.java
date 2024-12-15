package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.item.dto.comments.CommentDto;
import ru.practicum.shareit.item.dto.comments.CommentInputDto;
import ru.practicum.shareit.item.dto.items.ItemDto;
import ru.practicum.shareit.item.dto.items.ItemOutputDto;
import ru.practicum.shareit.item.service.ItemService;

import java.util.List;

/**
 * TODO Sprint add-controllers.
 */
@RestController
@RequestMapping("/items")
@RequiredArgsConstructor
@Slf4j
public class ItemController {

    private final ItemService itemService;

    @PostMapping
    public ResponseEntity<ItemOutputDto> createItem(@RequestHeader("X-Sharer-User-Id") Long id,
                                                    @RequestBody ItemDto itemDto) {
        log.info("\nServer: Получен запрос на добавление item {} от user {}", itemDto, id);
        itemDto.setOwner(id);
        ItemOutputDto addedItem = itemService.createItem(itemDto);
        log.info("\nWas added {}", addedItem);
        return ResponseEntity.ok(addedItem);
    }

    @PatchMapping("/{itemId}")
    public ResponseEntity<ItemOutputDto> updateItem(@RequestHeader("X-Sharer-User-Id") Long owner,
                                                    @PathVariable Long itemId,
                                                    @RequestBody ItemDto itemDto) {
        log.info("\nServer: Получен запрос на изменение item {} данных на {} от user {}", itemId, itemDto, owner);
        ItemOutputDto updatedItem = itemService.updateItem(owner, itemId, itemDto);
        log.info("\nWas updated {}", itemDto);
        return ResponseEntity.ok(updatedItem);
    }

    @DeleteMapping("/{itemId}")
    public ResponseEntity<ItemOutputDto> deleteItem(@RequestHeader("X-Sharer-User-Id") Long id,
                                                    @PathVariable Long itemId) {
        log.info("\nServer: Получен запрос на удаление item {} от user {}", itemId, id);
        ItemOutputDto deletedItem = itemService.deleteItem(id, itemId);
        log.info("\nWas deleted {}", deletedItem);
        return ResponseEntity.ok(deletedItem);
    }

    @GetMapping("/{itemId}")
    public ResponseEntity<ItemOutputDto> getItemById(@PathVariable Long itemId) {
        log.info("\nServer: Получен запрос на просмотр данных item {}", itemId);
        ItemOutputDto receivedItem = itemService.getItemById(itemId);
        log.info("\nWas received item {}", receivedItem);
        return ResponseEntity.ok(receivedItem);
    }

    @GetMapping
    public ResponseEntity<List<ItemOutputDto>> getAllItemsOfOwner(@RequestHeader("X-Sharer-User-Id") Long id) {
        log.info("\nServer: Получен запрос на получение всех items пользователя {}", id);
        List<ItemOutputDto> itemDtos = itemService.getAllItemsOfOwner(id);
        log.info("\nWas received list of {} elements", itemDtos.size());
        return ResponseEntity.ok(itemDtos);
    }

    // Get.../items/search?text={text}
    @GetMapping("/search")
    public ResponseEntity<List<ItemOutputDto>> getItemsByContext(@RequestParam(name = "text") String query) {
        log.info("\nПолучен запрос на получение всех items по контексту ...{}...", query);
        List<ItemOutputDto> receivedItems = itemService.getItemsByContext(query);
        log.info("\nWas received list by context of {} elements", receivedItems.size());
        return ResponseEntity.ok(receivedItems);
    }

    //POST /items/{itemId}/comment
    @PostMapping("/{itemId}/comment")
    public ResponseEntity<CommentDto> addNewComment(@RequestHeader("X-Sharer-User-Id") Long id,
                                    @PathVariable Long itemId,
                                    @RequestBody CommentInputDto newComment) {
        log.info("\nПолучен запрос на добавление комментария от пользователя {} по item {} textsize {}",
                id, itemId, newComment.getText().length());
        newComment.setItem(itemId);
        newComment.setAuthorName(id);
        CommentDto commentOutput = itemService.addNewComment(newComment);
        log.info("\nДобавлен комментарий {}", commentOutput);
        return ResponseEntity.ok(commentOutput);
    }

}
