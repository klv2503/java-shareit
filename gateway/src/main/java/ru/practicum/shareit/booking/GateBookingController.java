package ru.practicum.shareit.booking;

import jakarta.validation.constraints.NotNull;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import ru.practicum.shareit.auxiliary.validations.EnumValid;
import ru.practicum.shareit.auxiliary.validations.OnCreate;
import ru.practicum.shareit.booking.dto.BookItemRequestDto;
import ru.practicum.shareit.booking.dto.BookState;

@RestController
@RequestMapping(path = "/bookings")
@RequiredArgsConstructor
@Slf4j
@Validated
public class GateBookingController {
    private final BookingClient bookingClient;

    @GetMapping
    //Пока сделал пагинацию только для этого ендпойнта
    public ResponseEntity<Object> getBookings(@RequestHeader("X-Sharer-User-Id") @NotNull @Positive long userId,
                                              @RequestParam(name = "state", defaultValue = "all")
                                              @EnumValid(enumClass = BookState.class) String state,
                                              @PositiveOrZero @RequestParam(name = "from", defaultValue = "0") Integer from,
                                              @Positive @RequestParam(name = "size", defaultValue = "10") Integer size) {
        log.info("\nGateway: Get booking with state {}, userId={}, from={}, size={}", state, userId, from, size);
        return bookingClient.getBookings(userId, state, from, size);
    }

    @PostMapping
    public ResponseEntity<Object> bookItem(@RequestHeader("X-Sharer-User-Id") @NotNull @Positive long userId,
                                           @RequestBody @Validated(OnCreate.class) BookItemRequestDto requestDto) {
        log.info("\nGateway: Creating booking {}, userId={}", requestDto, userId);
        return bookingClient.bookItem(userId, requestDto);
    }

    @GetMapping("/{bookingId}")
    public ResponseEntity<Object> getBooking(@RequestHeader("X-Sharer-User-Id") @NotNull @Positive long userId,
                                             @PathVariable(name = "bookingId") @NotNull @Positive Long bookingId) {
        log.info("\nGateway: Get booking {}, userId={}", bookingId, userId);
        return bookingClient.getBooking(userId, bookingId);
    }

    //Patch /bookings/{bookingId}?approved={approved}
    @PatchMapping("/{bookingId}")
    public ResponseEntity<Object> approveBooking(@RequestHeader("X-Sharer-User-Id") @NotNull @Positive Long id,
                                                 @PathVariable(name = "bookingId") @NotNull @Positive Long bookingId,
                                                 @RequestParam @NotNull Boolean approved) {
        log.info("\nGateway: Получен запрос на подтверждение бронирования owner {}, booking {}, approve {}",
                id, bookingId, approved);
        return bookingClient.patchBooking(id, bookingId, approved);
    }

    //GET /bookings/owner?state={state}
    @GetMapping("/owner")
    public ResponseEntity<Object> getOwnersBookings(@RequestHeader("X-Sharer-User-Id") @NotNull @Positive Long id,
                                                    @RequestParam(defaultValue = "ALL")
                                                    @EnumValid(enumClass = BookState.class) String state) {
        log.info("\nGateway: Получен запрос на просмотр всех бронирований собственника {} state {}", id, state);
        return bookingClient.getOwnersBookings(id, state);
    }

}