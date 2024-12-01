package ru.practicum.shareit.booking;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.auxiliary.validations.EnumValid;
import ru.practicum.shareit.auxiliary.validations.OnCreate;
import ru.practicum.shareit.booking.dto.BookingInputDto;
import ru.practicum.shareit.booking.dto.BookingOutputDto;
import ru.practicum.shareit.booking.model.BookState;
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
@Validated
public class BookingController {

    private final BookingService bookingService;
    private final UserService userService;
    private final ItemService itemService;

    //Post /bookings
    @PostMapping
    public BookingOutputDto addNewBooking(@RequestHeader("X-Sharer-User-Id") @NotNull @Positive Long id,
                                          @Validated(OnCreate.class) @RequestBody BookingInputDto bookingInputDto) {
        log.info("\nПолучен запрос на добавление бронирования booker {}, {}", id, bookingInputDto);
        bookingInputDto.setBooker(id);
        if (bookingInputDto.getStatus() == null)
            bookingInputDto.setStatus("WAITING");
        BookingOutputDto result = bookingService.addNewBooking(bookingInputDto);
        log.info("Was added booking {}", result);
        return result;
    }

    //Patch /bookings/{bookingId}?approved={approved}
    @PatchMapping("/{bookingId}")
    public BookingOutputDto approveBooking(@RequestHeader("X-Sharer-User-Id") @NotNull @Positive Long id,
                                           @PathVariable @NotNull @Positive Long bookingId,
                                           @RequestParam @NotNull Boolean approved) {
        log.info("\nПолучен запрос на подтверждение бронирования owner {}, booking {}, approve {}",
                id, bookingId, approved);
        BookingOutputDto result = bookingService.approveBooking(id, bookingId, approved);
        log.info("Now booking {} has status {}", result.getId(), result.getStatus());
        return result;
    }

    //GET /bookings/{bookingId}
    @GetMapping("/{bookingId}")
    public BookingOutputDto getBookingInfo(@PathVariable @NotNull @Positive Long bookingId) {
        log.info("\nПолучен запрос на просмотр бронирования bookingId {}", bookingId);
        BookingOutputDto result = bookingService.getBookingInfo(bookingId);
        log.info("\nПолучен {}", result);
        return result;
    }

    //GET /bookings?state={state}
    @GetMapping
    public List<BookingOutputDto> getAllUsersBookings(@RequestHeader("X-Sharer-User-Id") @NotNull @Positive Long id,
                                                      @RequestParam(defaultValue = "ALL") @EnumValid(enumClass = BookState.class) String state) {
        log.info("\nПолучен запрос на просмотр всех бронирований user {} state {}", id, state);
        List<BookingOutputDto> result = bookingService.getAllUsersBookings(id, state);
        log.info("\nПолучен список из {} бронирований", result.size());
        return result;
    }

    //GET /bookings/owner?state={state}
    @GetMapping("/owner")
    public List<BookingOutputDto> getAllOwnersBookings(@RequestHeader("X-Sharer-User-Id") @NotNull @Positive Long id,
                                                       @RequestParam(defaultValue = "ALL") @EnumValid(enumClass = BookState.class) String state) {
        log.info("\nПолучен запрос на просмотр всех бронирований собственника {} state {}", id, state);
        User owner = userService.getUser(id);
        List<Item> ownersItems = itemService.getItemsList(owner);
        log.info("\nПолучен список из {} вещей собственника {}", ownersItems.size(), id);
        if (ownersItems.isEmpty())
            return List.of();
        List<BookingOutputDto> result = bookingService.getAllOwnersBookings(ownersItems, state);
        log.info("\nПолучен список из {} бронирований собственника {}", result.size(), id);
        return result;
    }
}
