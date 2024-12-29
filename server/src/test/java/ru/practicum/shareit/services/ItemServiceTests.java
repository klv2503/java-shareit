package ru.practicum.shareit.services;

import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.auxiliary.exceptions.AccessNotAllowedException;
import ru.practicum.shareit.auxiliary.exceptions.NotFoundException;
import ru.practicum.shareit.booking.dto.BookingInputDto;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.service.BookingService;
import ru.practicum.shareit.item.dto.comments.CommentInputDto;
import ru.practicum.shareit.item.dto.items.ItemDto;
import ru.practicum.shareit.item.dto.items.ItemMapper;
import ru.practicum.shareit.item.dto.items.ItemOutputDto;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.service.ItemService;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.dto.UserMapper;
import ru.practicum.shareit.user.service.UserService;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@Transactional
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.NONE)
@Sql({"/schema.sql", "/data.sql"})
@ActiveProfiles("test")
public class ItemServiceTests {

    private final EntityManager em;

    private final ItemService itemService;

    private final UserService userService;

    private final UserMapper userMapper;

    private final BookingService bookingService;

    @Test
    public void createItem_whenItemDtoCorrect_thenCreate() {
        ItemDto testedItem = ItemDto.builder()
                .name("Forth item")
                .description("Nothing to say")
                .available(true)
                .owner(3L)
                .build();
        itemService.createItem(testedItem);

        User user = userService.getUser(3L);
        TypedQuery<Item> query =
                em.createQuery("Select i from Item i where i.owner = :owner", Item.class);
        Item item = query.setParameter("owner", user)
                .getSingleResult();

        assertNotNull(item);
        ItemOutputDto itemFromBase = ItemMapper.mapItemToItemOutputDto(item);
        assertEquals(testedItem.getName(), itemFromBase.getName());
        assertEquals(testedItem.getDescription(), itemFromBase.getDescription());
    }

    @Test
    public void updateItem_whenItemDtoCorrect_thenUpdate() {
        ItemDto itemForUpdate = ItemDto.builder()
                .name("Forth item")
                .description("Nothing to say")
                .available(true)
                .owner(3L)
                .build();
        itemService.createItem(itemForUpdate);

        User user = userService.getUser(3L);
        TypedQuery<Item> query =
                em.createQuery("Select i from Item i where i.owner = :owner", Item.class);
        Item item = query.setParameter("owner", user)
                .getSingleResult();
        long itemId = item.getId();

        ItemDto itemWithNewData = ItemDto.builder()
                .name("Item without name")
                .description("Nothing to say")
                .available(true)
                .build();

        ItemOutputDto itemAfterUpdate = itemService.updateItem(3L, itemId, itemWithNewData);
        assertNotNull(itemAfterUpdate);
        assertEquals(itemWithNewData.getName(), itemAfterUpdate.getName());
        assertEquals(itemWithNewData.getDescription(), itemAfterUpdate.getDescription());
    }

    @Test
    public void updateItem_whenIncorrectOwner_thenNotAccess() {
        ItemDto itemForUpdate = ItemDto.builder()
                .name("Forth item")
                .description("Nothing to say")
                .available(true)
                .owner(3L)
                .build();
        itemService.createItem(itemForUpdate);

        User user = userService.getUser(3L);
        TypedQuery<Item> query =
                em.createQuery("Select i from Item i where i.owner = :owner", Item.class);
        Item item = query.setParameter("owner", user)
                .getSingleResult();
        long itemId = item.getId();

        ItemDto itemWithNewData = ItemDto.builder()
                .name("Item without name")
                .description("Nothing to say")
                .available(true)
                .build();

        assertThrows(AccessNotAllowedException.class, () -> itemService.updateItem(1L, itemId, itemWithNewData));
    }

    @Test
    public void deleteItem_whenDataCorrect_thenDelete() {
        long userId = 3L;
        ItemDto itemForDelete = ItemDto.builder()
                .name("Forth item")
                .description("Nothing to say")
                .available(true)
                .owner(userId)
                .build();
        itemService.createItem(itemForDelete);

        User user = userService.getUser(userId);
        TypedQuery<Item> query =
                em.createQuery("Select i from Item i where i.owner = :owner", Item.class);
        Item item = query.setParameter("owner", user)
                .getSingleResult();
        long itemId = item.getId();

        ItemOutputDto deletedItem = itemService.deleteItem(userId, itemId);
        assertNotNull(deletedItem);
        assertEquals(itemForDelete.getName(), deletedItem.getName());
        assertEquals(itemForDelete.getDescription(), deletedItem.getDescription());

    }

    @Test
    public void deleteItem_whenOwnerIncorrect_thenNotAccess() {
        long userId = 3L;
        ItemDto itemForDelete = ItemDto.builder()
                .name("Forth item")
                .description("Nothing to say")
                .available(true)
                .owner(userId)
                .build();
        itemService.createItem(itemForDelete);

        User user = userService.getUser(userId);
        TypedQuery<Item> query =
                em.createQuery("Select i from Item i where i.owner = :owner", Item.class);
        Item item = query.setParameter("owner", user)
                .getSingleResult();
        long itemId = item.getId();
        long anotherUserId = 1L;

        assertThrows(AccessNotAllowedException.class, () -> itemService.deleteItem(anotherUserId, itemId));
    }

        @Test
    public void shouldGetItemById() {
        long itemId = 2L;
        TypedQuery<Item> query =
                em.createQuery("Select i from Item i where i.id = :id", Item.class);
        Item item = query.setParameter("id", itemId)
                .getSingleResult();
        ItemOutputDto testItem = ItemMapper.mapItemToItemOutputDto(item);

        ItemOutputDto receivedItem = itemService.getItemById(itemId);

        assertNotNull(receivedItem);
        assertEquals(testItem, receivedItem);
    }

    @Test
    public void shouldGetAllItemsByOwner() {
        long userId = 1L;
        User user = userMapper.mapUserDtoToUser(userService.getUserById(userId));
        TypedQuery<Item> query =
                em.createQuery("Select i from Item i where i.owner = :owner", Item.class);
        List<Item> items = query.setParameter("owner", user)
                .getResultList();
        List<ItemOutputDto> testItems = ItemMapper.mapItemsListToItemOutputDtoList(items);

        List<ItemOutputDto> receivedItems = itemService.getAllItemsOfOwner(userId);

        assertNotNull(receivedItems);
        assertEquals(testItems, receivedItems);
    }

    @Test
    public void shouldGetAllItemsByContext() {

        System.out.println(itemService.getAllItemsOfOwner(1L));
        String context = "ir";
        TypedQuery<Item> query =
                em.createQuery("Select i from Item i where UPPER(i.name) LIKE UPPER(:context)", Item.class);
        List<Item> items = query.setParameter("context", "%" + context + "%")
                .getResultList();
        List<ItemOutputDto> testItems = ItemMapper.mapItemsListToItemOutputDtoList(items);

        List<ItemOutputDto> receivedItems = itemService.getItemsByContext(context);

        assertNotNull(receivedItems);
        assertEquals(2, receivedItems.size());
        assertEquals(testItems, receivedItems);
    }

    @Test
    public void getItem_whenCorrectId_thenGet() {
        Item item = itemService.getItem(1L);
        assertNotNull(item);
        assertEquals("First item", item.getName());
        assertEquals("Without description", item.getDescription());

    }

    @Test
    public void getItem_whenIncorrectId_thenNotFound() {

        assertThrows(NotFoundException.class, () -> itemService.getItem(100L));

    }

    @Test
    public void getItemsLastBooking_shouldGetBooking() {
        long itemId = 1L;
        BookingInputDto bookItemFirst = BookingInputDto.builder()
                .start(LocalDateTime.now().minusMinutes(10))
                .end(LocalDateTime.now().minusMinutes(9))
                .booker(3L)
                .itemId(itemId)
                .status("WAITING")
                .build();
        bookingService.addNewBooking(bookItemFirst);
        BookingInputDto bookItemSecond = BookingInputDto.builder()
                .start(LocalDateTime.now().minusMinutes(8))
                .end(LocalDateTime.now().minusMinutes(7))
                .booker(2L)
                .itemId(itemId)
                .status("WAITING")
                .build();
        bookingService.addNewBooking(bookItemSecond);

        Item item = itemService.getItem(itemId);
        TypedQuery<Booking> query =
                em.createQuery("Select b from Booking b where b.item = :item", Booking.class);
        List<Booking> bookings = query.setParameter("item", item)
                .getResultList();
        Booking result = itemService.getItemsLastBooking(bookings);
        assertNotNull(result);
        assertEquals(bookItemSecond.getStart(),result.getStart());
        assertEquals(bookItemSecond.getEnd(),result.getEnd());

    }

    @Test
    public void getItemsNextBooking_shouldGetBooking() {
        long itemId = 1L;
        BookingInputDto bookItemFirst = BookingInputDto.builder()
                .start(LocalDateTime.now().plusMinutes(5))
                .end(LocalDateTime.now().plusMinutes(6))
                .booker(3L)
                .itemId(itemId)
                .status("WAITING")
                .build();
        bookingService.addNewBooking(bookItemFirst);
        BookingInputDto bookItemSecond = BookingInputDto.builder()
                .start(LocalDateTime.now().plusMinutes(7))
                .end(LocalDateTime.now().plusMinutes(8))
                .booker(2L)
                .itemId(itemId)
                .status("WAITING")
                .build();
        bookingService.addNewBooking(bookItemSecond);

        Item item = itemService.getItem(itemId);
        TypedQuery<Booking> query =
                em.createQuery("Select b from Booking b where b.item = :item", Booking.class);
        List<Booking> bookings = query.setParameter("item", item)
                .getResultList();
        Booking result = itemService.getItemsNextBooking(bookings);
        assertNotNull(result);
        assertEquals(bookItemFirst.getStart(),result.getStart());
        assertEquals(bookItemFirst.getEnd(),result.getEnd());

    }

    @SneakyThrows
    @Test
    public void shouldAddComment() {
        long itemId = 1L;
        long bookerId = 3L;
        //Бронируем и подтверждаем бронирование
        BookingInputDto newBooking = BookingInputDto.builder()
                .start(LocalDateTime.now().plusSeconds(1L))
                .end(LocalDateTime.now().plusSeconds(2L))
                .itemId(itemId)
                .booker(bookerId)
                .status("WAITING")
                .build();
        bookingService.addNewBooking(newBooking);
        User user = userService.getUser(bookerId);
        TypedQuery<Booking> query =
                em.createQuery("Select b from Booking b where b.booker = :booker", Booking.class);
        Booking booking = query.setParameter("booker", user)
                .getSingleResult();
        long bookingId = booking.getId();
        bookingService.approveBooking(1L, bookingId, true);
        //Делаем паузу, чтобы бронирование осталось в прошлом
        Thread.sleep(3000);
        //Формируем и заносим в БД комментарий
        CommentInputDto commentary = CommentInputDto.builder()
                .text("good item")
                .item(itemId)
                .authorName(bookerId)
                .build();
        itemService.addNewComment(commentary);
        //Читаем коммент из базы
        User booker = userService.getUser(bookerId);
        TypedQuery<Comment> query2 =
                em.createQuery("Select c from Comment c where c.author = :author", Comment.class);
        Comment testedComment = query2.setParameter("author", booker).getSingleResult();

        assertNotNull(testedComment);
        assertEquals(itemId, testedComment.getItem().getId());
        assertEquals(bookerId, testedComment.getAuthor().getId());
        assertEquals("good item", testedComment.getText());

    }
}
