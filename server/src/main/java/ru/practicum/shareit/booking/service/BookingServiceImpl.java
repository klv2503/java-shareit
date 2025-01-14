package ru.practicum.shareit.booking.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.auxiliary.exceptions.AccessNotAllowedException;
import ru.practicum.shareit.auxiliary.exceptions.NotFoundException;
import ru.practicum.shareit.auxiliary.exceptions.ValidationException;
import ru.practicum.shareit.booking.model.BookStatus;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.BookingRepository;
import ru.practicum.shareit.booking.dto.BookingInputDto;
import ru.practicum.shareit.booking.dto.BookingInputMapper;
import ru.practicum.shareit.booking.dto.BookingOutputDto;
import ru.practicum.shareit.booking.dto.BookingOutputMapper;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.service.UserService;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;

@Service
@RequiredArgsConstructor
@Slf4j
public class BookingServiceImpl implements BookingService {

    private final BookingRepository repository;
    private final BookingInputMapper inputMapper;
    private final BookingOutputMapper outputMapper;
    private final UserService userService;
    private final ItemRepository itemRepository;

    @Override
    public BookingOutputDto addNewBooking(BookingInputDto bookingInputDto) {
        Booking booking = inputMapper.inputDtoToBooking(bookingInputDto);
        booking.setBooker(userService.getUser(bookingInputDto.getBooker()));
        Item item = itemRepository.findById(bookingInputDto.getItemId())
                .orElseThrow(() ->
                        new NotFoundException("Item " + bookingInputDto.getItemId() + " not found", bookingInputDto));
        if (Objects.equals(booking.getBooker().getId(), item.getOwner().getId()))
            throw new ValidationException("User can't book own item", booking);

        if (!item.getAvailable())
            throw new ValidationException("Item is not available", item);
        booking.setItem(item);
        return outputMapper.mapBookingToBookingOutputDto(repository.save(booking));
    }

    @Override
    public BookingOutputDto approveBooking(Long id, Long bookingId, Boolean approve) {
        Booking booking = getBooking(bookingId); //проверяем наличие бронирования
        if (!Objects.equals(booking.getItem().getOwner().getId(), id))
            throw new AccessNotAllowedException("User " + id + " is not owner of item " + booking.getItem().getId() +
                    " and can't approve booking " + bookingId, booking);
        if (approve) {
            booking.setStatus(BookStatus.APPROVED);
        } else {
            booking.setStatus(BookStatus.REJECTED);
        }
        return outputMapper.mapBookingToBookingOutputDto(repository.save(booking));
    }

    @Override
    public BookingOutputDto getBookingInfo(Long id) {
        return outputMapper.mapBookingToBookingOutputDto(getBooking(id));
    }

    @Override
    public List<BookingOutputDto> getAllUsersBookings(Long id, String state, Integer from, Integer size) {
        String upperState = state.toUpperCase();
        User booker = userService.getUser(id);
        int page = from / size;
        Pageable pageable = PageRequest.of(page, size);
        Page<Booking> bookingList = switch (upperState) {
            case "ALL" -> repository.findAllByBooker(booker, pageable);
            case "WAITING", "REJECTED" -> repository.findAllByBookerAndStatus(booker,
                    BookStatus.valueOf(upperState), pageable);
            case "PAST" -> repository.findAllByBookerAndEndBefore(booker, LocalDateTime.now(), pageable);
            case "FUTURE" -> repository.findAllByBookerAndStartAfter(booker, LocalDateTime.now(), pageable);
            case "CURRENT" -> repository.findCurrentUsersBookings(booker, LocalDateTime.now(), pageable);
            default -> throw new IllegalStateException("Unexpected value: " + state.toUpperCase());
        };
        return outputMapper.mapBookingListToDtoList(bookingList.getContent());
    }

    @Override
    public List<BookingOutputDto> getAllOwnersBookings(List<Item> ownersItems, String state) {
        String upperState = state.toUpperCase();
        List<Booking> bookingList = switch (upperState) {
            case "ALL" -> repository.findAllBookingsOfItems(ownersItems);
            case "WAITING", "REJECTED" -> repository.findBookingsOfItemByStatus(ownersItems,
                    BookStatus.valueOf(upperState));
            case "PAST" -> repository.findPastUsersBookings(ownersItems, LocalDateTime.now());
            case "FUTURE" -> repository.findFutureUsersBookings(ownersItems, LocalDateTime.now());
            case "CURRENT" -> repository.findCurrentBookingsOfItems(ownersItems, LocalDateTime.now());
            default -> throw new IllegalStateException("Unexpected value: " + state.toUpperCase());
        };
        return outputMapper.mapBookingListToDtoList(bookingList);
    }

    @Override
    public List<Booking> getAllItemsBookings(Item item) {
        return repository.findAllByItemAndStatus(item, BookStatus.WAITING);
    }

    @Override
    public Booking getBooking(Long id) {
        return repository.findById(id).orElseThrow(() -> new NotFoundException("Not found booking id = " + id, id));
    }

    public List<Booking> getPastUsersBookingOfItem(User user, Item item) {
        //первоначально в методе был отбор также и по статусу BookStatus.APPROVED
        return repository.findAllByBookerAndItemAndEndBefore(
                user, item, LocalDateTime.now());
    }
}
