package ru.practicum.shareit.item.dto;

import org.springframework.stereotype.Component;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.model.PairOfDate;

@Component
public class ItemWithCommentsMapper {
    public static ItemWithCommentsDto toItemWithCommentsDto(Item item) {
        ItemWithCommentsDto itemWithCommentsDto = new ItemWithCommentsDto();
        itemWithCommentsDto.setId(item.getId());
        itemWithCommentsDto.setName(item.getName());
        itemWithCommentsDto.setDescription(item.getDescription());
        itemWithCommentsDto.setAvailable(item.getAvailable());
        if (item.getOwner() != null)
            itemWithCommentsDto.setOwner(item.getOwner());
        if (item.getRequest() != null)
            itemWithCommentsDto.setRequest(item.getRequest());
        if (item.getLastBooking() != null)
            itemWithCommentsDto.setLastBooking(new PairOfDate(item.getLastBooking().getStart(),
                    item.getLastBooking().getEnd()));
        if (item.getNextBooking() != null)
            itemWithCommentsDto.setNextBooking(new PairOfDate(item.getNextBooking().getStart(),
                    item.getNextBooking().getEnd()));
        if (item.getComments() != null)
            itemWithCommentsDto.setComments(item.getComments());
        return itemWithCommentsDto;
    }

}
