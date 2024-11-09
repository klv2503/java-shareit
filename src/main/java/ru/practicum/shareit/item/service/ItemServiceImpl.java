package ru.practicum.shareit.item.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exceptions.AccessNotAllowedException;
import ru.practicum.shareit.exceptions.NotFoundException;
import ru.practicum.shareit.item.ItemMemoryStorage;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemMapper;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.service.UserService;

import java.util.List;
import java.util.Objects;

@Service
@RequiredArgsConstructor
public class ItemServiceImpl implements ItemService {

    private final ItemMemoryStorage itemStorage;
    private final ItemMapper itemMapper;
    private final UserService userService;

    @Override
    public ItemDto createItem(ItemDto itemDto) {
        UserDto userDto = userService.getUserById(itemDto.getOwner()); //Проверка существования user
        return itemMapper.mapItemToItemDto(itemStorage.addNewItem(itemMapper.mapItemDtoToItem(itemDto)));
    }

    @Override
    public ItemDto updateItem(Long owner, Long itemId, ItemDto itemDto) {
        //Проверка существования item
        Item oldItem = itemStorage.getItemById(itemId);
        if (oldItem == null)
            throw new NotFoundException("Not found item id = " + itemId, itemDto);

        //Проверка того, что пользователь является собственником
        if (oldItem.getOwner() != owner)
            throw new AccessNotAllowedException("User " + owner + " can't make changes to item " + itemId);

        //Проверка, не пытается ли собственник изменить защищенное поле
        if (!Objects.equals(oldItem.getRequest(), itemDto.getRequest()))
            throw new AccessNotAllowedException("Changing of item's request is forbidden");

        //Заносим новые данные в разрешенные к изменениям поля
        if (itemDto.getName() != null && !itemDto.getName().isBlank())
            oldItem.setName(itemDto.getName());
        if (itemDto.getDescription() != null && !itemDto.getDescription().isBlank())
            oldItem.setDescription(itemDto.getDescription());
        if (itemDto.getAvailable() != null)
            oldItem.setAvailable(itemDto.getAvailable());

        return itemMapper.mapItemToItemDto(itemStorage.updateItem(oldItem));
    }

    @Override
    public ItemDto deleteItem(Long id, Long itemId) {
        Item item = itemStorage.getItemById(itemId);
        if (item == null)
            throw new NotFoundException("Not found item id = " + itemId, itemId);
        if (!item.getOwner().equals(id))
            throw new AccessNotAllowedException("Request not sent by owner. Deleting is forbidden");
        return itemMapper.mapItemToItemDto(itemStorage.deleteItem(itemId));
    }

    @Override
    public ItemDto getItemById(Long id) {
        return itemMapper.mapItemToItemDto(itemStorage.getItemById(id));
    }

    @Override
    public List<ItemDto> getAllItemsOfOwner(Long id) {
        return itemMapper.mapItemsListToItemDtoList(itemStorage.getAllOwnersItems(id));
    }

    @Override
    public List<ItemDto> getItemsByContext(String query) {
        return query == null || query.isEmpty() ? List.of() :
                itemMapper.mapItemsListToItemDtoList(itemStorage.getItemsByContext(query));
    }

}
