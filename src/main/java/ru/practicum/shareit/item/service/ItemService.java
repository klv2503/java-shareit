package ru.practicum.shareit.item.service;

import ru.practicum.shareit.item.dto.*;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.User;

import java.util.List;

public interface ItemService {

    ItemOutputDto createItem(ItemDto itemDto);

    ItemOutputDto updateItem(Long owner, Long itemId, ItemDto itemDto);

    ItemOutputDto deleteItem(Long id, Long itemId);

    ItemWithCommentsDto getItemById(Long id);

    List<ItemWithBookingDto> getAllItemsOfOwner(Long l);

    List<ItemOutputDto> getItemsByContext(String query);

    Item getItem(Long id);

    List<Item> getItemsList(User owner);

    CommentDto addNewComment(CommentInput commentInput);
}
