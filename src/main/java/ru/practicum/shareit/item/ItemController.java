package ru.practicum.shareit.item;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.auxiliary.validations.NotEmptyItemDto;
import ru.practicum.shareit.auxiliary.validations.OnCreate;
import ru.practicum.shareit.item.dto.*;
import ru.practicum.shareit.item.service.ItemService;

import java.util.List;

/**
 * TODO Sprint add-controllers.
 */
@RestController
@RequestMapping("/items")
@RequiredArgsConstructor
@Validated
@Slf4j
public class ItemController {

    private final ItemService itemService;

    @PostMapping
    public ItemOutputDto createItem(@RequestHeader("X-Sharer-User-Id") @NotNull @Positive Long id,
                                    @Validated(OnCreate.class) @RequestBody ItemDto itemDto) {
        log.info("\nПолучен запрос на добавление item {} от user {}", itemDto, id);
        itemDto.setOwner(id);
        ItemOutputDto addedItem = itemService.createItem(itemDto);
        log.info("\nWas added {}", addedItem);
        return addedItem;
    }

    @PatchMapping("/{itemId}")
    public ItemOutputDto updateItem(@RequestHeader("X-Sharer-User-Id") @NotNull @Positive Long owner,
                                    @PathVariable @NotNull @Positive Long itemId,
                                    @RequestBody @NotEmptyItemDto ItemDto itemDto) {
        log.info("\nПолучен запрос на изменение item {} данных на {} от user {}", itemId, itemDto, owner);
        ItemOutputDto updatedItem = itemService.updateItem(owner, itemId, itemDto);
        log.info("\nWas updated {}", itemDto);
        return updatedItem;
    }

    @DeleteMapping("/{itemId}")
    public ItemOutputDto deleteItem(@RequestHeader("X-Sharer-User-Id") @NotNull @Positive Long id,
                                    @PathVariable @NotNull @Positive Long itemId) {
        log.info("\nПолучен запрос на удаление item {} от user {}", itemId, id);
        ItemOutputDto deletedItem = itemService.deleteItem(id, itemId);
        log.info("\nWas deleted {}", deletedItem);
        return deletedItem;
    }

    @GetMapping("/{itemId}")
    public ItemWithCommentsDto getItemById(@PathVariable @NotNull @Positive Long itemId) {
        log.info("\nПолучен запрос на просмотр данных item {}", itemId);
        ItemWithCommentsDto receivedItem = itemService.getItemById(itemId);
        log.info("\nWas received item {}", receivedItem);
        return receivedItem;
    }

    @GetMapping
    public List<ItemWithBookingDto> getAllItemsOfOwner(@RequestHeader("X-Sharer-User-Id") @NotNull @Positive Long id) {
        log.info("\nПолучен запрос на получение всех items пользователя {}", id);
        List<ItemWithBookingDto> itemDtos = itemService.getAllItemsOfOwner(id);
        log.info("\nWas received list of {} elements", itemDtos.size());
        return itemDtos;
    }

    ///items/search?text={text}
    @GetMapping("/search")
    public List<ItemOutputDto> getItemsByContext(@RequestParam(name = "text") String query) {
        log.info("\nПолучен запрос на получение всех items по контексту ...{}...", query);
        List<ItemOutputDto> receivedItems = itemService.getItemsByContext(query);
        log.info("\nWas received list by context of {} elements", receivedItems.size());
        return receivedItems;
    }

    //POST /items/{itemId}/comment
    @PostMapping("/{itemId}/comment")
    public CommentDto addNewComment(@RequestHeader("X-Sharer-User-Id") @NotNull @Positive Long id,
                                    @PathVariable @NotNull @Positive Long itemId,
                                    @RequestBody @Valid CommentInput newComment) {
        log.info("\nПолучен запрос на добавление комментария от пользователя {} по item {} textsize {}",
                id, itemId, newComment.getText().length());
        newComment.setItem(itemId);
        newComment.setAuthorName(id);
        CommentDto commentOutputDto = itemService.addNewComment(newComment);
        log.info("\nДобавлен комментарий {}", commentOutputDto);
        return commentOutputDto;
    }

}
