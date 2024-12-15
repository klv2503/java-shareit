package ru.practicum.shareit;

import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.dto.BookingInputDto;
import ru.practicum.shareit.booking.service.BookingService;
import ru.practicum.shareit.item.dto.comments.CommentInputDto;
import ru.practicum.shareit.item.dto.items.ItemDto;
import ru.practicum.shareit.item.dto.items.ItemMapper;
import ru.practicum.shareit.item.dto.items.ItemOutputDto;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.service.ItemService;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.dto.UserMapper;
import ru.practicum.shareit.user.service.UserService;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@Transactional
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.NONE)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@ActiveProfiles("test")
public class ItemServiceTests {

    private final EntityManager em;

    private final ItemService itemService;

    private final UserService userService;

    private final UserMapper userMapper;

    private final BookingService bookingService;

    @BeforeAll
    public void setTestData() {
        UserDto firstUserDto = new UserDto("Name of User", "some@email.com");
        userService.createUser(firstUserDto);
        UserDto secondUserDto = new UserDto("Second user", "second@email.com");
        userService.createUser(secondUserDto);
        UserDto thirdUserDto = new UserDto("Third user", "third@email.com");
        userService.createUser(thirdUserDto);

        ItemDto firstItem = ItemDto.builder()
                .name("First item")
                .description("Without description")
                .available(true)
                .owner(1L)
                .build();
        itemService.createItem(firstItem);
        ItemDto secondItem = ItemDto.builder()
                .name("Second item")
                .description("To long description")
                .available(true)
                .owner(2L)
                .build();
        itemService.createItem(secondItem);
        ItemDto thirdItem = ItemDto.builder()
                .name("Third item")
                .description("Another description")
                .available(true)
                .owner(1L)
                .build();
        itemService.createItem(thirdItem);

        //Create and approve Booking

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

    @SneakyThrows
    @Test
    public void shouldAddComment() {
        long itemId = 1L;
        long booker = 3L;
        //Бронируем и подтверждаем бронирование
        BookingInputDto newBooking = BookingInputDto.builder()
                .start(LocalDateTime.now().plusSeconds(1L))
                .end(LocalDateTime.now().plusSeconds(2L))
                .itemId(itemId)
                .booker(booker)
                .status("WAITING")
                .build();
        bookingService.addNewBooking(newBooking);
        bookingService.approveBooking(1L, 1L, true);
        //Делаем паузу, чтобы бронирование осталось в прошлом
        Thread.sleep(3000);
        //Формируем и заносим в БД комментарий
        CommentInputDto commentary = CommentInputDto.builder()
                .text("good item")
                .item(itemId)
                .authorName(booker)
                .build();
        itemService.addNewComment(commentary);
        //Читаем коммент из базы
        User user = userService.getUser(booker);
        TypedQuery<Comment> query =
                em.createQuery("Select c from Comment c where c.author = :author", Comment.class);
        Comment testedComment = query.setParameter("author", user).getSingleResult();

        assertNotNull(testedComment);
        assertEquals(itemId, testedComment.getItem().getId());
        assertEquals(booker, testedComment.getAuthor().getId());
        assertEquals("good item", testedComment.getText());

    }
}
