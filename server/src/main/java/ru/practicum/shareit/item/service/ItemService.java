package ru.practicum.shareit.item.service;

import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.item.dto.comments.CommentDto;
import ru.practicum.shareit.item.dto.comments.CommentInputDto;
import ru.practicum.shareit.item.dto.items.ItemDto;
import ru.practicum.shareit.item.dto.items.ItemOutputDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.User;

import java.util.List;

public interface ItemService {

    ItemOutputDto createItem(ItemDto itemDto);

    ItemOutputDto updateItem(Long owner, Long itemId, ItemDto itemDto);

    ItemOutputDto deleteItem(Long id, Long itemId);

    ItemOutputDto getItemById(Long id);

    List<ItemOutputDto> getAllItemsOfOwner(Long l);

    List<ItemOutputDto> getItemsByContext(String query);

    Item getItem(Long id);

    List<Item> getItemsList(User owner);

    CommentDto addNewComment(CommentInputDto commentInput);

    Booking getItemsLastBooking(List<Booking> bookings);

    Booking getItemsNextBooking(List<Booking> bookings);
}
