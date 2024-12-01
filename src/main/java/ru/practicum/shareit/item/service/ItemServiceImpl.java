package ru.practicum.shareit.item.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.logging.log4j.util.Strings;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.auxiliary.exceptions.AccessNotAllowedException;
import ru.practicum.shareit.auxiliary.exceptions.NotFoundException;
import ru.practicum.shareit.auxiliary.exceptions.ValidationException;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.service.BookingService;
import ru.practicum.shareit.item.dto.*;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.repository.CommentRepository;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.service.UserService;

import java.time.LocalDateTime;
import java.util.*;

@Service
@RequiredArgsConstructor
@Slf4j
public class ItemServiceImpl implements ItemService {

    private final ItemRepository repository;
    private final UserService userService;
    private final BookingService bookingService;
    private final CommentRepository commentRepository;

    @Override
    public ItemOutputDto createItem(ItemDto itemDto) {
        User user = userService.getUser(itemDto.getOwner()); //Проверка существования user
        Item item = ItemMapper.mapItemDtoToItem(itemDto);
        return ItemMapper.mapItemToItemOutputDto(repository.save(item));
    }

    @Override
    public ItemOutputDto updateItem(Long owner, Long itemId, ItemDto itemDto) {
        //Проверка существования item
        Item oldItem = getItem(itemId);
        //Проверка того, что пользователь является собственником
        if (!oldItem.getOwner().getId().equals(owner))
            throw new AccessNotAllowedException("User " + owner + " can't make changes to item " + itemId, oldItem);

        //Проверка, не пытается ли собственник изменить защищенное поле
        if ((itemDto.getRequest() != null) && !Objects.equals(oldItem.getRequest().getId(), itemDto.getRequest()))
            throw new AccessNotAllowedException("Changing of item's request is forbidden", oldItem);

        //Заносим новые данные в разрешенные к изменениям поля
        if (!Strings.isBlank(itemDto.getName()))
            oldItem.setName(itemDto.getName());
        if (!Strings.isBlank(itemDto.getDescription()))
            oldItem.setDescription(itemDto.getDescription());
        if (itemDto.getAvailable() != null)
            oldItem.setAvailable(itemDto.getAvailable());

        return ItemMapper.mapItemToItemOutputDto(repository.save(oldItem));
    }

    @Override
    public ItemOutputDto deleteItem(Long id, Long itemId) {
        Item item = getItem(itemId);
        if (!item.getOwner().getId().equals(id))
            throw new AccessNotAllowedException("Request not sent by owner. Deleting is forbidden", item);
        repository.deleteById(itemId);
        return ItemMapper.mapItemToItemOutputDto(item);
    }

    @Override
    public ItemWithCommentsDto getItemById(Long id) {
        Item item = getItem(id);
        List<Booking> itemsBooking = bookingService.getAllItemsBookings(item);
        item.setLastBooking(getItemsLastBooking(itemsBooking));
        item.setNextBooking(getItemsNextBooking(itemsBooking));
        List<Comment> comments = commentRepository.findAllByItem(item);
        item.setComments(ShortCommentMapper.mapCommentListToShortCommentList(comments));
        return ItemWithCommentsMapper.toItemWithCommentsDto(item);
    }

    @Override
    public List<ItemWithBookingDto> getAllItemsOfOwner(Long id) {
        User owner = userService.getUser(id); //Проверка существования owner
        List<Item> items = getItemsList(owner);
        for (Item i : items) {
            List<Booking> bookingsOfItem = bookingService.getAllItemsBookings(i);
            i.setLastBooking(getItemsLastBooking(bookingsOfItem));
            i.setNextBooking(getItemsNextBooking(bookingsOfItem));
        }
        return ItemWithBookingMapper.itemWithBookingDtosList(getItemsList(owner));
    }

    @Override
    public List<ItemOutputDto> getItemsByContext(String query) {
        if (Strings.isBlank(query))
            return List.of();
        return ItemMapper
                .mapItemsListToItemOutputDtoList(repository.findAllByAvailableTrueAndNameContainingIgnoreCase(query));
    }

    @Override
    public Item getItem(Long id) {
        return repository.findById(id).orElseThrow(() -> new NotFoundException("Not found item id = " + id, id));
    }

    @Override
    public List<Item> getItemsList(User owner) {
        return repository.findAllByOwner(owner);
    }

    @Override
    public CommentDto addNewComment(CommentInput newComment) {
        User user = userService.getUser(newComment.getAuthorName()); //Проверка существования пользователя
        Item item = getItem(newComment.getItem()); //Проверка существования item
        //проверка, что пользователь действительно пользовался вещью
        List<Booking> bookings = bookingService.getPastUsersBookingOfItem(user, item);
        log.info("\nList of bookings {}", bookings);
        if (bookings.isEmpty())
            throw new ValidationException("User " + user.getId() + " not used item " + item.getId() +
                    ". Comment is prohibited", newComment);
        Comment comment = new Comment(newComment.getText(), item, user);
        return CommentMapper.mapCommentToCommentDto(commentRepository.save(comment));
    }

    private Booking getItemsLastBooking(List<Booking> bookings) {
        LocalDateTime now = LocalDateTime.now();
        return bookings.stream()
                .filter(b -> b.getEnd().isBefore(now))
                .max(Comparator.comparing(Booking::getStart))
                .orElse(null);
    }

    private Booking getItemsNextBooking(List<Booking> bookings) {
        LocalDateTime now = LocalDateTime.now();
        return bookings.stream()
                .filter(b -> b.getStart().isAfter(now))
                .min(Comparator.comparing(Booking::getStart))
                .orElse(null);
    }
}
