package ru.practicum.shareit;

import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;
import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.item.dto.items.ItemDto;
import ru.practicum.shareit.item.service.ItemService;
import ru.practicum.shareit.request.dto.RequestDto;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.request.service.RequestService;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.service.UserService;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@Transactional
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.NONE)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@ActiveProfiles("test")
public class RequestServiceTests {

    private final EntityManager em;

    private final RequestService requestService;

    private final UserService userService;

    private final ItemService itemService;

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
    }

    @Test
    public void shouldAddNewRequest() {

        System.out.println(userService.getAllUsers());
        long requestorId = 3L;
        RequestDto newRequest = RequestDto.builder()
                .description("Abracadabra")
                .build();
        requestService.addNewRequest(requestorId, newRequest);
        User user = new User(3L,"Third user", "third@email.com");

        TypedQuery<ItemRequest> query =
                em.createQuery("Select r from ItemRequest r where r.requestor = :requestor", ItemRequest.class);
        ItemRequest itemRequest = query.setParameter("requestor", user)
                .getSingleResult();

        assertNotNull(itemRequest);
        assertEquals(itemRequest.getDescription(), newRequest.getDescription());

    }

    @Test
    public void shouldGetAllUsersRequest() {

        System.out.println(userService.getAllUsers());
        long requestorId = 3L;
        RequestDto newRequest = RequestDto.builder()
                .description("Abracadabra")
                .build();
        requestService.addNewRequest(requestorId, newRequest);
        RequestDto newRequest2 = RequestDto.builder()
                .description("Once more")
                .build();
        requestService.addNewRequest(requestorId, newRequest2);
        User user = new User(3L,"Third user", "third@email.com");

        TypedQuery<ItemRequest> query =
                em.createQuery("Select r from ItemRequest r where r.requestor = :requestor", ItemRequest.class);
        List<ItemRequest> itemRequests = query.setParameter("requestor", user)
                .getResultList();

        assertNotNull(itemRequests);
        assertEquals(2, itemRequests.size());
        ItemRequest actual = itemRequests.get(1);
        assertEquals(actual.getDescription(), newRequest2.getDescription());

    }

    @Test
    public void shouldGetAllRequestsOfAnotherUsers() {

        System.out.println(userService.getAllUsers());
        long requestorId = 3L;
        RequestDto newRequest = RequestDto.builder()
                .description("Abracadabra")
                .build();
        requestService.addNewRequest(requestorId, newRequest);
        requestorId = 1L;
        RequestDto newRequest2 = RequestDto.builder()
                .description("Once more")
                .build();
        requestService.addNewRequest(requestorId, newRequest2);
        User user = new User(2L,"Third user", "third@email.com");

        TypedQuery<ItemRequest> query =
                em.createQuery("Select r from ItemRequest r where r.requestor <> :requestor", ItemRequest.class);
        List<ItemRequest> itemRequests = query.setParameter("requestor", user)
                .getResultList();

        assertNotNull(itemRequests);
        assertEquals(2, itemRequests.size());
        ItemRequest actual = itemRequests.get(1);
        assertEquals(actual.getDescription(), newRequest2.getDescription());

    }

    @Test
    public void shouldGetRequestById() {

        System.out.println(userService.getAllUsers());
        long requestorId = 3L;
        RequestDto newRequest = RequestDto.builder()
                .description("Abracadabra")
                .build();
        requestService.addNewRequest(requestorId, newRequest);
        requestorId = 1L;
        RequestDto newRequest2 = RequestDto.builder()
                .description("Once more")
                .build();
        requestService.addNewRequest(requestorId, newRequest2);
        long requestId = 2L;

        TypedQuery<ItemRequest> query =
                em.createQuery("Select r from ItemRequest r where r.id = :id", ItemRequest.class);
        ItemRequest itemRequest = query.setParameter("id", requestId)
                .getSingleResult();

        assertNotNull(itemRequest);
        assertEquals(itemRequest.getDescription(), newRequest2.getDescription());

    }

}
