package ru.practicum.shareit.item.dto;

import org.springframework.stereotype.Component;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.model.PairOfDate;

import java.util.List;

@Component
public class ItemWithBookingMapper {

    public static ItemWithBookingDto toItemWithBookingDto(Item item) {
        ItemWithBookingDto itemWithBookingDto = new ItemWithBookingDto();
        itemWithBookingDto.setId(item.getId());
        itemWithBookingDto.setName(item.getName());
        itemWithBookingDto.setDescription(item.getDescription());
        itemWithBookingDto.setAvailable(item.getAvailable());
        if (item.getOwner() != null)
            itemWithBookingDto.setOwner(item.getOwner());
        if (item.getRequest() != null)
            itemWithBookingDto.setRequest(item.getRequest());
        if (item.getLastBooking() != null)
            itemWithBookingDto.setLastBooking(new PairOfDate(item.getLastBooking().getStart(),
                    item.getLastBooking().getEnd()));
        if (item.getNextBooking() != null)
            itemWithBookingDto.setNextBooking(new PairOfDate(item.getNextBooking().getStart(),
                    item.getNextBooking().getEnd()));
        return itemWithBookingDto;
    }

    public static List<ItemWithBookingDto> itemWithBookingDtosList(List<Item> items) {
        return items.stream()
                .map(ItemWithBookingMapper::toItemWithBookingDto)
                .toList();
    }
}
