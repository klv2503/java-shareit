package ru.practicum.shareit.item.service;

import ru.practicum.shareit.item.dto.ItemDto;

import java.util.List;

public interface ItemService {

    ItemDto createItem(ItemDto itemDto);

    ItemDto updateItem(Long owner, Long itemId, ItemDto itemDto);

    ItemDto deleteItem(Long id, Long itemId);

    ItemDto getItemById(Long id);

    List<ItemDto> getAllItemsOfOwner(Long l);

    List<ItemDto> getItemsByContext(String query);

}
