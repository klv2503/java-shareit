package ru.practicum.shareit.services;

import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;
import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.request.dto.RequestDto;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.request.service.RequestService;
import ru.practicum.shareit.user.User;

import java.util.Comparator;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@Transactional
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.NONE)
@Sql({"/schema.sql", "/data.sql"})
@ActiveProfiles("test")
public class RequestServiceTests {

    private final EntityManager em;

    private final RequestService requestService;

    @Test
    public void shouldAddNewRequest() {

        long requestorId = 3L;
        RequestDto newRequest = RequestDto.builder()
                .description("Abracadabra")
                .build();
        requestService.addNewRequest(requestorId, newRequest);
        User user = new User(3L, "Third user", "third@email.com");

        TypedQuery<ItemRequest> query =
                em.createQuery("Select r from ItemRequest r where r.requestor = :requestor", ItemRequest.class);
        ItemRequest itemRequest = query.setParameter("requestor", user)
                .getSingleResult();

        assertNotNull(itemRequest);
        assertEquals(itemRequest.getDescription(), newRequest.getDescription());

    }

    @Test
    public void shouldGetAllUsersRequest() {

        //Adding 2 new requests of user 3
        long requestorId = 3L;
        RequestDto newRequest = RequestDto.builder()
                .description("Abracadabra")
                .build();
        requestService.addNewRequest(requestorId, newRequest);
        RequestDto newRequest2 = RequestDto.builder()
                .description("Once more")
                .build();
        requestService.addNewRequest(requestorId, newRequest2);

        List<RequestDto> usersRequests = requestService.getUsersRequests(requestorId).stream()
                .sorted(Comparator.comparing(RequestDto::getDescription))
                .toList();

        assertNotNull(usersRequests);
        assertEquals(2, usersRequests.size());
        RequestDto actual = usersRequests.get(1);
        assertEquals(newRequest2.getDescription(), actual.getDescription());

    }

    @Test
    public void shouldGetAllRequestsOfAnotherUsers() {

        //Adding 2 new requests of user 3
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

        long userId = 2L;
        List<RequestDto> alienRequests = requestService.getAllAnotherUsersRequests(userId).stream()
                .sorted(Comparator.comparing(RequestDto::getDescription))
                .toList();

        assertNotNull(alienRequests);
        assertEquals(2, alienRequests.size());
        RequestDto actual = alienRequests.get(1);
        assertEquals(actual.getDescription(), newRequest2.getDescription());

    }

    @Test
    public void shouldGetRequestById() {

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
        RequestDto usersRequest = requestService.getRequestById(requestorId, requestId);

        assertNotNull(usersRequest);
        assertEquals(usersRequest.getDescription(), newRequest2.getDescription());

    }

    @Test
    public void getRequest_whenCorrectId_thenGet() {
        long requestorId = 3L;
        RequestDto newRequest = RequestDto.builder()
                .description("Abracadabra")
                .build();
        requestService.addNewRequest(requestorId, newRequest);
        User user = new User(requestorId, "Third user", "third@email.com");

        TypedQuery<ItemRequest> query =
                em.createQuery("Select r from ItemRequest r where r.requestor = :requestor", ItemRequest.class);
        ItemRequest itemRequest = query.setParameter("requestor", user)
                .getSingleResult();
        long requestId = itemRequest.getId();

        ItemRequest testedRequest = requestService.getRequest(requestId);

        assertNotNull(itemRequest);
        assertEquals(itemRequest, testedRequest);
    }

}
