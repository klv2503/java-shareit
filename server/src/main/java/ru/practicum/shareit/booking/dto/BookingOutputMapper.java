package ru.practicum.shareit.booking.dto;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;
import ru.practicum.shareit.auxiliary.EnumConverter;
import ru.practicum.shareit.booking.model.Booking;

import java.util.List;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING, uses = EnumConverter.class)
public interface BookingOutputMapper {

    @Mapping(source = "status", target = "status")
    BookingOutputDto mapBookingToBookingOutputDto(Booking booking);

    List<BookingOutputDto> mapBookingListToDtoList(List<Booking> bookingList);

}
