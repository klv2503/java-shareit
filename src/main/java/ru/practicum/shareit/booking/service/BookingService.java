package ru.practicum.shareit.booking.service;

import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.dto.BookingInputDto;
import ru.practicum.shareit.booking.dto.BookingOutputDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.User;

import java.util.List;

public interface BookingService {
    BookingOutputDto addNewBooking(BookingInputDto bookingInputDto);

    BookingOutputDto approveBooking(Long id, Long bookingId, Boolean approve);

    BookingOutputDto getBookingInfo(Long id);

    List<BookingOutputDto> getAllUsersBookings(Long id, String state);

    List<BookingOutputDto> getAllOwnersBookings(List<Item> items, String state);

    Booking getBooking(Long id);

    List<Booking> getPastUsersBookingOfItem(User user, Item item);

    List<Booking> getAllItemsBookings(Item item);
}
