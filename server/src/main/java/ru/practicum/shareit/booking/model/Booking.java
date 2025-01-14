package ru.practicum.shareit.booking.model;

import jakarta.persistence.*;
import lombok.*;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.User;

import java.time.LocalDateTime;

/**
 * TODO Sprint add-bookings.
 */
@Entity
@Table(name = "bookings")
@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class Booking {

    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;                    // уникальный идентификатор бронирования;

    @Column(name = "start_date")
    private LocalDateTime start;        // дата и время начала бронирования;

    @Column(name = "end_date")
    private LocalDateTime end;          // дата и время конца бронирования;

    @ManyToOne(optional = false)
    @JoinColumn(name = "item_id")
    private Item item;                  // вещь, которую пользователь бронирует;

    @ManyToOne
    @JoinColumn(name = "booker_id", nullable = false)
    private User booker;                // пользователь, который осуществляет бронирование;

    @Enumerated(EnumType.STRING)
    @Column(name = "status")
    private BookStatus status;          // статус бронирования
}
