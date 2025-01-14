package ru.practicum.shareit.booking.dto;

import org.springframework.stereotype.Component;
import ru.practicum.shareit.booking.model.BookStatus;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.User;

@Component
public class BookingInputMapper {

    public Booking inputDtoToBooking(BookingInputDto bookingInputDto) {
        Booking booking = new Booking();
        booking.setId(bookingInputDto.getId());
        booking.setStart(bookingInputDto.getStart());
        booking.setEnd(bookingInputDto.getEnd());
        booking.setItem(new Item(bookingInputDto.getItemId()));
        booking.setBooker(new User(bookingInputDto.getBooker()));
        booking.setStatus(BookStatus.valueOf(bookingInputDto.getStatus()));
        return booking;
    }
}
