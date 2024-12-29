package ru.practicum.shareit.mappers;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.model.BookStatus;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.item.dto.items.ItemDto;
import ru.practicum.shareit.item.dto.items.ItemMapper;
import ru.practicum.shareit.item.dto.items.ItemOutputDto;
import ru.practicum.shareit.item.dto.items.ShortItemDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.service.ItemService;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.service.UserService;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@Transactional
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.NONE)
@Sql({"/schema.sql", "/data.sql"})
@ActiveProfiles("test")

public class ItemMapperTests {

    private final ItemService itemService;

    private final UserService userService;

    @Test
    public void mapItemDtoToItem_whenRequestId_thenItemHasRequestId() {
        ItemDto itemDto = ItemDto.builder()
                .id(1L)
                .name("simple name")
                .description("nothing")
                .owner(1L)
                .available(true)
                .requestId(1L)
                .build();
        Item item = ItemMapper.mapItemDtoToItem(itemDto);
        assertNotNull(item.getRequest());
        assertEquals(itemDto.getRequestId(), item.getRequest().getId());
    }

    @Test
    public void mapItemToShort_shouldCorrectlyConvertItemToShortItem() {
        Item item = itemService.getItem(1L);

        ShortItemDto shortItemDto = ItemMapper.mapItemToShort(item);

        assertEquals(1L, shortItemDto.getItemId());
        assertEquals("First item", shortItemDto.getName());
        assertEquals(1, shortItemDto.getOwnerId());
    }

    @Test
    public void mapItemsListToShortItemsList_shouldCorrectlyConvert() {
        User user = userService.getUser(1L);
        List<Item> items = itemService.getItemsList(user);

        List<ShortItemDto> shortItemDto = ItemMapper.mapItemsListToShortItemsList(items).stream()
                .sorted(Comparator.comparing(ShortItemDto::getItemId))
                .toList();

        assertNotNull(shortItemDto);
        assertEquals(3, shortItemDto.size());
        assertEquals(4, shortItemDto.getLast().getItemId());
    }

    @Test
    public  void mapItemToItemOutputDto_shouldCorrectlyConvert() {
        Item item = itemService.getItem(1L);
        User requestor = userService.getUser(3L);
        User newBooker = userService.getUser(2L);
        ItemRequest request = ItemRequest.builder()
                .id(1L)
                .description("nothing")
                .requestor(requestor)
                .created(LocalDateTime.now().minusDays(1L))
                .items(null)
                .build();
        Booking lastBooking = new Booking(1L, LocalDateTime.now().minusDays(2L), LocalDateTime.now().minusDays(1L),
                item, requestor, BookStatus.APPROVED);
        Booking nextBooking = new Booking(2L, LocalDateTime.now().plusDays(2L), LocalDateTime.now().plusDays(3L),
                item, newBooker, BookStatus.WAITING);
        item.setRequest(request);
        item.setLastBooking(lastBooking);
        item.setNextBooking(nextBooking);
        item.setComments(new ArrayList<>());

        ItemOutputDto output = ItemMapper.mapItemToItemOutputDto(item);

        assertNotNull(output);
        assertEquals(item.getOwner(), output.getOwner());
        assertEquals(request, output.getRequest());
        assertEquals(lastBooking.getStart(), output.getLastBooking().getStartDateTime());
        assertEquals(lastBooking.getEnd(), output.getLastBooking().getEndDateTime());
        assertEquals(nextBooking.getStart(), output.getNextBooking().getStartDateTime());
        assertEquals(nextBooking.getEnd(), output.getNextBooking().getEndDateTime());
        assertEquals(item.getComments(), output.getComments());

    }
}
