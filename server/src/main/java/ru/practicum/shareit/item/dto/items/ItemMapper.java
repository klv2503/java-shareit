package ru.practicum.shareit.item.dto.items;

import org.springframework.stereotype.Component;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.model.PairOfDate;
import ru.practicum.shareit.request.model.ItemRequest;
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
        if (itemDto.getRequestId() != null)
            item.setRequest(ItemRequest.builder().id(itemDto.getRequestId()).build());
        return item;
    }

    public static ItemOutputDto mapItemToItemOutputDto(Item item) {
        ItemOutputDto itemOutputDto = new ItemOutputDto();
        itemOutputDto.setId(item.getId());
        itemOutputDto.setName(item.getName());
        itemOutputDto.setDescription(item.getDescription());
        itemOutputDto.setAvailable(item.getAvailable());
        if (item.getOwner() != null)
            itemOutputDto.setOwner(item.getOwner());
        if (item.getRequest() != null)
            itemOutputDto.setRequest(item.getRequest());
        if (item.getLastBooking() != null)
            itemOutputDto.setLastBooking(new PairOfDate(item.getLastBooking().getStart(),
                    item.getLastBooking().getEnd()));
        if (item.getNextBooking() != null)
            itemOutputDto.setNextBooking(new PairOfDate(item.getNextBooking().getStart(),
                    item.getNextBooking().getEnd()));
        if (item.getComments() != null)
            itemOutputDto.setComments(item.getComments());
        return itemOutputDto;
    }

    public static List<ItemOutputDto> mapItemsListToItemOutputDtoList(List<Item> items) {
        return items.stream()
                .map(ItemMapper::mapItemToItemOutputDto)
                .toList();
    }

    public static ShortItemDto mapItemToShort(Item item) {
        return ShortItemDto.builder()
                .itemId(item.getId())
                .name(item.getName())
                .ownerId(item.getOwner().getId())
                .build();
    }

    public static List<ShortItemDto> mapItemsListToShortItemsList(List<Item> items) {
        return items.stream()
                .map(ItemMapper::mapItemToShort)
                .toList();
    }

}
