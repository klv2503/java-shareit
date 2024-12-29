package ru.practicum.shareit.item.model;

import jakarta.persistence.*;
import lombok.*;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.item.dto.comments.ShortCommentDto;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.user.User;

import java.util.ArrayList;
import java.util.List;

/**
 * TODO Sprint add-controllers.
 */
@Entity
@Table(name = "items")
@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Item {

    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id; //— уникальный идентификатор вещи;

    @Column(name = "name")
    private String name; //— краткое название;

    @Column(name = "description")
    private String description; //— развёрнутое описание;

    @Column(name = "is_available")
    private Boolean available; //— статус о том, доступна или нет вещь для аренды;

    @ManyToOne
    @JoinColumn(name = "owner_id", nullable = false)
    private User owner; //— владелец вещи;

    @ManyToOne(optional = true)
    @JoinColumn(name = "request_id", nullable = true)
    private ItemRequest request; //— ссылка на соответствующий запрос, по которому вещь была создана

    //Ниже перечислены transient-поля
    @Transient
    private Booking lastBooking;

    @Transient
    private Booking nextBooking;

    @Transient
    private List<ShortCommentDto> comments = new ArrayList<>();

    public Item(Long id) {
        this.id = id;
    }

}