package ru.practicum.shareit.request.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import ru.practicum.shareit.auxiliary.exceptions.NotFoundException;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.request.dto.RequestDto;
import ru.practicum.shareit.request.dto.RequestMapper;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.request.repository.RequestRepository;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.service.UserService;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class RequestServiceImpl implements RequestService {

    private final RequestRepository repository;
    private final ItemRepository itemRepository;
    private final UserService userService;


    @Override
    public RequestDto addNewRequest(long id, RequestDto requestDto) {
        requestDto.setRequestor(id);
        ItemRequest itemRequest = RequestMapper.mapRequestDtoToItemRequest(requestDto);
        itemRequest.setCreated(LocalDateTime.now());
        return RequestMapper.mapItemRequestToRequestDto(repository.save(itemRequest));
    }

    @Override
    // Для каждого запроса должны быть указаны описание, дата и время создания, а также список
    //ответов в формате: id вещи, название, id владельца. В дальнейшем, используя указанные id вещей,
    // можно будет получить подробную информацию о каждой из них. Запросы должны возвращаться отсортированными от
    // более новых к более старым.
    public List<RequestDto> getUsersRequests(long id) {
        User requestor = userService.getUser(id); //получаем данные пользователя для запроса, проверяя существование
        //Получаем список запросов, заодно его сортируем
        List<ItemRequest> requestsList = repository.findByRequestorOrderByCreatedDesc(requestor);
        if (CollectionUtils.isEmpty(requestsList))
            return List.of();
        Map<Long, ItemRequest> requests = requestsList.stream()
                .collect(Collectors.toMap(ItemRequest::getId, Function.identity()));
        Map<Long, List<Item>> items = itemRepository.findAllByRequestIn(requestsList).stream()
                .collect(Collectors.groupingBy(item -> item.getRequest().getId()));
        for (ItemRequest req : requestsList) {
            req.setItems(items.getOrDefault(req.getId(), null));
        }
        return RequestMapper.mapItemRequestListToRequestDtoList(requestsList);
    }

    @Override
    //получить список запросов, созданных другими пользователями.
    //С помощью этого эндпоинта пользователи смогут просматривать существующие запросы, на которые они
    //могли бы ответить. Запросы сортируются по дате создания от более новых к более старым.
    public List<RequestDto> getAllAnotherUsersRequests(long id) {
        User requestor = userService.getUser(id); //получаем данные пользователя для запроса, проверяя существование
        List<ItemRequest> requests = repository.findByRequestorNotOrderByCreatedDesc(requestor);
        return RequestMapper.mapItemRequestListToRequestDtoList(requests);
    }

    @Override
    public RequestDto getRequestById(long id, long requestId) {
        User user = userService.getUser(id);
        ItemRequest itemRequest = getRequest(requestId);
        itemRequest.setItems(itemRepository.findAllByRequestIn(List.of(itemRequest)));
        return RequestMapper.mapItemRequestToRequestDto(itemRequest);
    }

    @Override
    public ItemRequest getRequest(Long id) {
        return repository.findById(id)
                .orElseThrow(() -> new NotFoundException("Request not found", id));
    }
}
