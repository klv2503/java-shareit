package ru.practicum.shareit.booking;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.booking.dto.BookingInputDto;
import ru.practicum.shareit.booking.dto.BookingOutputDto;
import ru.practicum.shareit.booking.service.BookingService;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.service.ItemService;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.service.UserService;

import java.util.List;

/**
 * TODO Sprint add-bookings.
 */
@RestController
@RequestMapping(path = "/bookings")
@Slf4j
@RequiredArgsConstructor
public class BookingController {

    private final BookingService bookingService;
    private final UserService userService;
    private final ItemService itemService;

    //Post /bookings
    @PostMapping
    public ResponseEntity<BookingOutputDto> addNewBooking(@RequestHeader("X-Sharer-User-Id") Long id,
                                                          @RequestBody BookingInputDto bookingInputDto) {
        log.info("\nServer: Получен запрос на добавление бронирования booker {}, {}", id, bookingInputDto);
        bookingInputDto.setBooker(id);
        if (bookingInputDto.getStatus() == null)
            bookingInputDto.setStatus("WAITING");
        BookingOutputDto result = bookingService.addNewBooking(bookingInputDto);
        log.info("\nWas added booking {}", result);
        return ResponseEntity.ok(result);
    }

    //GET /bookings/{bookingId}
    @GetMapping("/{bookingId}")
    public ResponseEntity<BookingOutputDto> getBookingInfo(@PathVariable Long bookingId) {
        log.info("\nServer: Получен запрос на просмотр бронирования bookingId {}", bookingId);
        BookingOutputDto result = bookingService.getBookingInfo(bookingId);
        log.info("\nПолучен {}", result);
        return ResponseEntity.ok(result);
    }

    //GET /bookings?state={state}
    @GetMapping
    public ResponseEntity<List<BookingOutputDto>> getAllUsersBookings(@RequestHeader("X-Sharer-User-Id") Long id,
                                                                      @RequestParam(defaultValue = "ALL") String state,
                                                                      @RequestParam(name = "from") Integer from,
                                                                      @RequestParam(name = "size") Integer size) {
        log.info("\nServer: Получен запрос на просмотр всех бронирований user {} state {} from {} size {}",
                id, state, from, size);
        List<BookingOutputDto> result = bookingService.getAllUsersBookings(id, state, from, size);
        log.info("\nПолучен список из {} бронирований", result.size());
        return ResponseEntity.ok(result);
    }

    //Patch /bookings/{bookingId}?approved={approved}
    @PatchMapping("/{bookingId}")
    public ResponseEntity<BookingOutputDto> approveBooking(@RequestHeader("X-Sharer-User-Id") Long id,
                                                           @PathVariable Long bookingId,
                                                           @RequestParam Boolean approved) {
        log.info("\nServer: Получен запрос на подтверждение бронирования owner {}, booking {}, approve {}",
                id, bookingId, approved);
        BookingOutputDto result = bookingService.approveBooking(id, bookingId, approved);
        log.info("\nNow booking {} has status {}", result.getId(), result.getStatus());
        return ResponseEntity.ok(result);
    }

    //GET /bookings/owner?state={state}
    @GetMapping("/owner")
    public ResponseEntity<List<BookingOutputDto>> getAllOwnersBookings(@RequestHeader("X-Sharer-User-Id") Long id,
                                                                       @RequestParam(defaultValue = "ALL") String state) {
        log.info("\nПолучен запрос на просмотр всех бронирований собственника {} state {}", id, state);
        User owner = userService.getUser(id);
        List<Item> ownersItems = itemService.getItemsList(owner);
        log.info("\nПолучен список из {} вещей собственника {}", ownersItems.size(), id);
        List<BookingOutputDto> result =
                ownersItems.isEmpty() ? List.of() : bookingService.getAllOwnersBookings(ownersItems, state);
        log.info("\nПолучен список из {} бронирований собственника {}", result.size(), id);
        return ResponseEntity.ok(result);
    }
}
