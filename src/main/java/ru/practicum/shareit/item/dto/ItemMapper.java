package ru.practicum.shareit.item.dto;

import org.springframework.stereotype.Component;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.request.ItemRequest;
import ru.practicum.shareit.user.User;

import java.util.List;

@Component
public class ItemMapper {

    public static Item mapItemDtoToItem(ItemDto itemDto) {
        Item item = new Item();
        item.setId(itemDto.getId());
        item.setName(itemDto.getName());
        item.setDescription(itemDto.getDescription());
        item.setAvailable(itemDto.getAvailable());
        item.setOwner(new User(itemDto.getOwner()));
        if (itemDto.getRequest() != null)
            item.setRequest(new ItemRequest(itemDto.getRequest()));
        return item;
    }

    public static ItemOutputDto mapItemToItemOutputDto(Item item) {
        ItemOutputDto itemOutputDto = new ItemOutputDto();
        itemOutputDto.setId(item.getId());
        itemOutputDto.setName(item.getName());
        itemOutputDto.setDescription(item.getDescription());
        itemOutputDto.setAvailable(item.getAvailable());
        itemOutputDto.setOwner(item.getOwner());
        itemOutputDto.setRequest(item.getRequest());
        return itemOutputDto;
    }

    public static List<ItemOutputDto> mapItemsListToItemOutputDtoList(List<Item> items) {
        return items.stream()
                .map(ItemMapper::mapItemToItemOutputDto)
                .toList();
    }
}
