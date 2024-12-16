package ru.practicum.shareit.request.dto;

import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;
import ru.practicum.shareit.item.dto.items.ItemMapper;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.user.User;

import java.util.List;

@Component
public class RequestMapper {

    public static ItemRequest mapRequestDtoToItemRequest(RequestDto requestDto) {
        return ItemRequest.builder()
                .description(requestDto.getDescription())
                .requestor(new User(requestDto.getRequestor()))
                .build();
    }

    public static RequestDto mapItemRequestToRequestDto(ItemRequest request) {
        return RequestDto.builder()
                .id(request.getId())
                .description(request.getDescription())
                .requestor(request.getRequestor().getId())
                .created(request.getCreated())
                .items(CollectionUtils.isEmpty(request.getItems()) ? null
                        : ItemMapper.mapItemsListToShortItemsList(request.getItems()))
                .build();
    }

    public static List<RequestDto> mapItemRequestListToRequestDtoList(List<ItemRequest> requests) {
        return requests.stream()
                .map(RequestMapper::mapItemRequestToRequestDto)
                .toList();
    }

}
