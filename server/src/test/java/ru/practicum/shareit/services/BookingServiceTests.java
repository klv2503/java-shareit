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
import ru.practicum.shareit.auxiliary.exceptions.NotFoundException;
import ru.practicum.shareit.auxiliary.exceptions.ValidationException;
import ru.practicum.shareit.booking.dto.BookingInputDto;
import ru.practicum.shareit.booking.dto.BookingOutputDto;
import ru.practicum.shareit.booking.dto.BookingOutputMapper;
import ru.practicum.shareit.booking.model.BookStatus;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.service.BookingService;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.service.UserService;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@Transactional
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.NONE)
@Sql({"/schema.sql", "/data.sql"})
@ActiveProfiles("test")
public class BookingServiceTests {

    private final EntityManager em;

    private final BookingService bookingService;

    private final BookingOutputMapper bookingOutputMapper;

    private final ItemRepository itemRepository;

    private final UserService userService;

    //private final ServerErrorHandler handler;

    //Testing addNewBooking
    @Test
    public void shouldAddNewBooking() {
        BookingInputDto newBooking = BookingInputDto.builder()
                .start(LocalDateTime.now().plusSeconds(1L))
                .end(LocalDateTime.now().plusSeconds(2L))
                .itemId(1L)
                .booker(3L)
                .status("WAITING")
                .build();
        bookingService.addNewBooking(newBooking);

        Item item = itemRepository.findById(newBooking.getItemId())
                .orElseThrow(() ->
                        new NotFoundException("Item " + newBooking.getItemId() + " not found", newBooking));
        User booker = userService.getUser(newBooking.getBooker());
        TypedQuery<Booking> query =
                em.createQuery("Select b from Booking b where b.item = :item AND b.booker = :booker", Booking.class);
        Booking receivedBooking = query.setParameter("item", item).setParameter("booker", booker)
                .getSingleResult();

        assertNotNull(receivedBooking);
        assertEquals(newBooking.getItemId(), receivedBooking.getItem().getId());
        assertEquals(newBooking.getBooker(), receivedBooking.getBooker().getId());
        assertEquals(newBooking.getStatus(), receivedBooking.getStatus().toString());

    }

    @Test
    public void shouldGetValidationExceptionWhenBookingOfOwnItem() {
        BookingInputDto newBooking = BookingInputDto.builder()
                .start(LocalDateTime.now().plusSeconds(1L))
                .end(LocalDateTime.now().plusSeconds(2L))
                .itemId(1L)
                .booker(1L)
                .status("WAITING")
                .build();
        assertThrows(ValidationException.class, () -> bookingService.addNewBooking(newBooking));
    }

    @Test
    public void shouldGetNotFoundExceptionWithBookingOfNotExistedItem() {
        BookingInputDto newBooking = BookingInputDto.builder()
                .start(LocalDateTime.now().plusSeconds(1L))
                .end(LocalDateTime.now().plusSeconds(2L))
                .itemId(100L)
                .booker(3L)
                .status("WAITING")
                .build();
        assertThrows(NotFoundException.class, () -> bookingService.addNewBooking(newBooking));
    }

    @Test
    public void shouldGetValidationExceptionWithBookingOfNotAvailableItem() {
        BookingInputDto newBooking = BookingInputDto.builder()
                .start(LocalDateTime.now().plusSeconds(1L))
                .end(LocalDateTime.now().plusSeconds(2L))
                .itemId(4L)
                .booker(3L)
                .status("WAITING")
                .build();
        assertThrows(ValidationException.class, () -> bookingService.addNewBooking(newBooking));
        //verify(bookingService, times(1)).handler.handlerMyValidation(ValidationException e);

        //assertEquals("Item is not available", e.message());
    }

    //Testing approveBooking
    @Test
    public void shouldApproveBooking() {
        BookingInputDto newBooking = BookingInputDto.builder()
                .start(LocalDateTime.now().plusSeconds(1L))
                .end(LocalDateTime.now().plusSeconds(2L))
                .itemId(1L)
                .booker(3L)
                .status("WAITING")
                .build();
        bookingService.addNewBooking(newBooking);

        Item item = itemRepository.findById(newBooking.getItemId())
                .orElseThrow(() ->
                        new NotFoundException("Item " + newBooking.getItemId() + " not found", newBooking));
        User booker = userService.getUser(newBooking.getBooker());
        TypedQuery<Booking> query =
                em.createQuery("Select b from Booking b where b.item = :item AND b.booker = :booker", Booking.class);
        Booking receivedBooking = query.setParameter("item", item).setParameter("booker", booker)
                .getSingleResult();
        long bookingId = receivedBooking.getId();
        bookingService.approveBooking(1L, bookingId, true);
        query = em.createQuery("Select b from Booking b where b.id = :id", Booking.class);

        Booking bookingAfterApprove = query.setParameter("id", bookingId).getSingleResult();

        assertNotNull(bookingAfterApprove);
        assertEquals(newBooking.getItemId(), bookingAfterApprove.getItem().getId());
        assertEquals(newBooking.getBooker(), bookingAfterApprove.getBooker().getId());
        assertEquals(BookStatus.APPROVED, bookingAfterApprove.getStatus());
    }

    @Test
    public void approveBooking_whenApproveNotExistedBooking_thenNotFoundException() {
        assertThrows(NotFoundException.class, () -> bookingService.approveBooking(1L,100L, true));
    }

    @Test
    public void getBooking_whenCorrectId_thenGet() {
        BookingInputDto newBooking = BookingInputDto.builder()
                .start(LocalDateTime.now().plusSeconds(1L))
                .end(LocalDateTime.now().plusSeconds(2L))
                .itemId(1L)
                .booker(3L)
                .status("WAITING")
                .build();
        bookingService.addNewBooking(newBooking);
        User user = userService.getUser(3L);
        TypedQuery<Booking> query =
                em.createQuery("Select b from Booking b where b.booker = :booker", Booking.class);
        Booking receivedBooking = query.setParameter("booker", user).getSingleResult();
        long bookingId = receivedBooking.getId();

        Booking testedBooking = bookingService.getBooking(bookingId);
        assertNotNull(testedBooking);
        assertEquals(newBooking.getItemId(), testedBooking.getItem().getId());
        assertEquals(newBooking.getBooker(), testedBooking.getBooker().getId());
        assertEquals(BookStatus.WAITING, testedBooking.getStatus());
    }

    @Test
    public void getBooking_whenIncorrectId_thenNotFound() {
        BookingInputDto newBooking = BookingInputDto.builder()
                .start(LocalDateTime.now().plusSeconds(1L))
                .end(LocalDateTime.now().plusSeconds(2L))
                .itemId(1L)
                .booker(3L)
                .status("WAITING")
                .build();
        bookingService.addNewBooking(newBooking);
        assertThrows(NotFoundException.class, () -> bookingService.getBooking(100L));
    }

    @Test
    public void getBookingInfo_whenCorrectId_thenGet() {
        BookingInputDto newBooking = BookingInputDto.builder()
                .start(LocalDateTime.now().plusSeconds(1L))
                .end(LocalDateTime.now().plusSeconds(2L))
                .itemId(1L)
                .booker(3L)
                .status("WAITING")
                .build();
        bookingService.addNewBooking(newBooking);
        User user = userService.getUser(3L);
        TypedQuery<Booking> query =
                em.createQuery("Select b from Booking b where b.booker = :booker", Booking.class);
        Booking receivedBooking = query.setParameter("booker", user).getSingleResult();
        long bookingId = receivedBooking.getId();

        BookingOutputDto testedBooking = bookingService.getBookingInfo(bookingId);
        assertNotNull(testedBooking);
        assertEquals(newBooking.getItemId(), testedBooking.getItem().getId());
        assertEquals(newBooking.getBooker(), testedBooking.getBooker().getId());
        assertEquals(BookStatus.WAITING, testedBooking.getStatus());
    }

    @Test
    public void getBookingInfo_whenIncorrectId_thenNotFound() {
        BookingInputDto newBooking = BookingInputDto.builder()
                .start(LocalDateTime.now().plusSeconds(1L))
                .end(LocalDateTime.now().plusSeconds(2L))
                .itemId(1L)
                .booker(3L)
                .status("WAITING")
                .build();
        bookingService.addNewBooking(newBooking);
        assertThrows(NotFoundException.class, () -> bookingService.getBooking(100L));
    }

    @Test
    public void shouldGetAllUsersBooking() {
        BookingInputDto booking1 = BookingInputDto.builder()
                .start(LocalDateTime.now().plusSeconds(1L))
                .end(LocalDateTime.now().plusSeconds(2L))
                .itemId(1L)
                .booker(3L)
                .status("WAITING")
                .build();
        bookingService.addNewBooking(booking1);

        BookingInputDto booking2 = BookingInputDto.builder()
                .start(LocalDateTime.now().plusSeconds(1L))
                .end(LocalDateTime.now().plusSeconds(2L))
                .itemId(2L)
                .booker(3L)
                .status("WAITING")
                .build();
        bookingService.addNewBooking(booking2);

        TypedQuery<Booking> query =
                em.createQuery("Select b from Booking b where b.status = :status", Booking.class);

        List<Booking> bookingList = query.setParameter("status", BookStatus.WAITING)
                .getResultList();
        List<BookingOutputDto> bookControlList = bookingOutputMapper.mapBookingListToDtoList(bookingList);

        List<BookingOutputDto> bookListForTest =
                bookingService.getAllUsersBookings(3L, "WAITING", 0, 10);

        assertNotNull(bookListForTest);
        assertEquals(2, bookListForTest.size());
        assertEquals(bookControlList, bookListForTest);

        bookListForTest =
                bookingService.getAllUsersBookings(3L, "ALL", 0, 10);

        assertNotNull(bookListForTest);
        assertEquals(2, bookListForTest.size());
        assertEquals(bookControlList, bookListForTest);

        bookListForTest =
                bookingService.getAllUsersBookings(3L, "PAST", 0, 10);

        assertEquals(0, bookListForTest.size());

        bookListForTest =
                bookingService.getAllUsersBookings(3L, "FUTURE", 0, 10);

        assertEquals(2, bookListForTest.size());

        bookListForTest =
                bookingService.getAllUsersBookings(3L, "CURRENT", 0, 10);

        assertEquals(0, bookListForTest.size());

    }

    @Test
    public void shouldGetAllOwnersBooking() {
        //Готовим бронирования
        BookingInputDto booking1 = BookingInputDto.builder()
                .start(LocalDateTime.now().plusSeconds(1L))
                .end(LocalDateTime.now().plusSeconds(2L))
                .itemId(1L)
                .booker(3L)
                .status("WAITING")
                .build();
        bookingService.addNewBooking(booking1);

        BookingInputDto booking2 = BookingInputDto.builder()
                .start(LocalDateTime.now().plusSeconds(1L))
                .end(LocalDateTime.now().plusSeconds(2L))
                .itemId(3L)
                .booker(2L)
                .status("WAITING")
                .build();
        bookingService.addNewBooking(booking2);

        //Готовим проверочный список
        TypedQuery<Booking> query =
                em.createQuery("Select b from Booking b where b.status = :status", Booking.class);
        List<Booking> bookingList = query.setParameter("status", BookStatus.WAITING)
                .getResultList();
        List<BookingOutputDto> bookControlList = bookingOutputMapper.mapBookingListToDtoList(bookingList);

        //Читаем список средствами приложения
        User user = userService.getUser(1L);
        List<Item> items = itemRepository.findAllByOwner(user);
        List<BookingOutputDto> bookListForTest =
                bookingService.getAllOwnersBookings(items, "WAITING");

        assertNotNull(bookListForTest);
        assertEquals(2, bookListForTest.size());
        assertEquals(bookControlList, bookListForTest);

        bookListForTest =
                bookingService.getAllOwnersBookings(items, "ALL");

        assertNotNull(bookListForTest);
        assertEquals(2, bookListForTest.size());
        assertEquals(bookControlList, bookListForTest);

        bookListForTest =
                bookingService.getAllOwnersBookings(items, "PAST");

        assertEquals(0, bookListForTest.size());

        bookListForTest =
                bookingService.getAllOwnersBookings(items, "FUTURE");

        assertEquals(2, bookListForTest.size());

        bookListForTest =
                bookingService.getAllOwnersBookings(items, "CURRENT");

        assertEquals(0, bookListForTest.size());

    }

}
