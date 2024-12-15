package ru.practicum.shareit.booking;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import ru.practicum.shareit.booking.model.BookStatus;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.User;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface BookingRepository extends JpaRepository<Booking, Long> {

    Page<Booking> findAllByBooker(User booker, Pageable pageable);

    List<Booking> findAllByItemAndStatus(Item item, BookStatus status);

    Page<Booking> findAllByBookerAndStatus(User booker, BookStatus status, Pageable pageable);

    Page<Booking> findAllByBookerAndEndBefore(User booker, LocalDateTime dateTime, Pageable pageable);

    Page<Booking> findAllByBookerAndStartAfter(User booker, LocalDateTime dateTime, Pageable pageable);

    //Запрос для получения текущих бронирований пользователя
    @Query(value =
            "SELECT b FROM Booking b WHERE (b.booker=:user) AND (:now BETWEEN b.start AND b.end)")
    Page<Booking> findCurrentUsersBookings(@Param("user") User user, @Param("now") LocalDateTime now, Pageable pageable);

    //Запрос для получения всех бронирований вещей собственника (state = ALL)
    @Query(value = "SELECT b FROM Booking b WHERE b.item in :itemsList")
    List<Booking> findAllBookingsOfItems(@Param("itemsList") List<Item> items);

    //Запрос для получения WAITING or REJECTED бронирований вещей собственника
    @Query(value =
            "SELECT b FROM Booking b WHERE (b.item in :itemsList) AND (b.status = :state)")
    List<Booking> findBookingsOfItemByStatus(@Param("itemsList") List<Item> items, @Param("state") BookStatus state);

    //Запрос для получения FUTURE бронирований вещей собственника
    @Query(value =
            "SELECT b FROM Booking b WHERE (b.item in :itemsList) AND (b.start > :now)")
    List<Booking> findFutureUsersBookings(@Param("itemsList") List<Item> items, @Param("now") LocalDateTime now);

    //Запрос для получения PAST бронирований вещей собственника
    @Query(value =
            "SELECT b FROM Booking b WHERE (b.item in :itemsList) AND (b.end < :now)")
    List<Booking> findPastUsersBookings(@Param("itemsList") List<Item> items, @Param("now") LocalDateTime now);

    //Запрос для получения CURRENT бронирований вещей собственника
    @Query(value =
            "SELECT b FROM Booking b WHERE (b.item in :itemsList) AND (:now BETWEEN b.start AND b.end)")
    List<Booking> findCurrentBookingsOfItems(@Param("itemsList") List<Item> items, @Param("now") LocalDateTime now);

    //Список завершенных (или в процессе выполнения) подтвержденных бронирований item user'ом
    List<Booking> findAllByBookerAndItemAndEndBefore(
            User booker, Item item, LocalDateTime thisDate);

}
