package ru.practicum.shareit.item;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.validations.NotEmptyItemDto;
import ru.practicum.shareit.validations.OnCreate;
import ru.practicum.shareit.item.dto.ItemDto;
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
    public ItemDto createItem(@RequestHeader("X-Sharer-User-Id") @NotNull @Positive Long id,
                              @Validated(OnCreate.class) @RequestBody ItemDto itemDto) {
        log.info("\nПолучен запрос на добавление item {} от user {}", itemDto, id);
        itemDto.setOwner(id);
        ItemDto addedItem = itemService.createItem(itemDto);
        log.info("\nWas added {}", addedItem);
        return addedItem;
    }

    @PatchMapping("/{itemId}")
    public ItemDto updateItem(@RequestHeader("X-Sharer-User-Id") @NotNull @Positive Long owner,
                              @PathVariable @NotNull @Positive Long itemId,
                              @RequestBody @NotEmptyItemDto ItemDto itemDto) {
        log.info("\nПолучен запрос на изменение item {} данных на {} от user {}", itemId, itemDto, owner);
        ItemDto updatedItem = itemService.updateItem(owner, itemId, itemDto);
        log.info("\nWas updated {}", itemDto);
        return updatedItem;
    }

    @DeleteMapping("/{itemId}")
    public ItemDto deleteItem(@RequestHeader("X-Sharer-User-Id") @NotNull @Positive Long id,
                              @PathVariable @NotNull @Positive Long itemId) {
        log.info("\nПолучен запрос на удаление item {} от user {}", itemId, id);
        ItemDto deletedItem = itemService.deleteItem(id, itemId);
        log.info("\nWas deleted {}", deletedItem);
        return deletedItem;
    }

    @GetMapping("/{itemId}")
    public ItemDto getItemById(@PathVariable @NotNull @Positive Long itemId) {
        //Смотреть может только пользователь или вообще кто угодно? = необходима ли проверка заголовка
        log.info("\nПолучен запрос на просмотр данных item {}", itemId);
        ItemDto receivedItem = itemService.getItemById(itemId);
        log.info("\nWas received item {}", receivedItem);
        return receivedItem;
    }

    @GetMapping
    public List<ItemDto> getAllItemsOfOwner(@RequestHeader("X-Sharer-User-Id") @NotNull @Positive Long id) {
        log.info("\nПолучен запрос на получение всех items пользователя {}", id);
        List<ItemDto> itemDtos = itemService.getAllItemsOfOwner(id);
        log.info("\nWas received list of {} elements", itemDtos.size());
        return itemDtos;
    }

    ///items/search?text={text}
    @GetMapping("/search")
    public List<ItemDto> getItemsByContext(@RequestParam(name = "text") String query) {
        log.info("\nПолучен запрос на получение всех items по контексту ...{}...", query);
        List<ItemDto> receivedItems = itemService.getItemsByContext(query);
        log.info("\nWas received list by context of {} elements", receivedItems.size());
        return receivedItems;
    }

}
