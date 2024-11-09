package ru.practicum.shareit.item.dto;

import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;
import ru.practicum.shareit.item.model.Item;

import java.util.List;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface ItemMapper {

    Item mapItemDtoToItem(ItemDto itemDto);

    ItemDto mapItemToItemDto(Item item);

    List<Item> mapItemDtoListToItemsList(List<ItemDto> itemDtos);

    List<ItemDto> mapItemsListToItemDtoList(List<Item> items);
}
