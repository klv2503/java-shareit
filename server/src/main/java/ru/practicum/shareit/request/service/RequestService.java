package ru.practicum.shareit.request.service;

import ru.practicum.shareit.request.dto.RequestDto;
import ru.practicum.shareit.request.model.ItemRequest;

import java.util.List;

public interface RequestService {

    RequestDto addNewRequest(long id, RequestDto requestDto);

    List<RequestDto> getUsersRequests(long id);

    List<RequestDto> getAllAnotherUsersRequests(long id);

    RequestDto getRequestById(long id, long requestId);

    ItemRequest getRequest(Long id);
}
