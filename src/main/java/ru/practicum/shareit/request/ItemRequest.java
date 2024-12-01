package ru.practicum.shareit.request;

import jakarta.persistence.*;
import lombok.*;
import ru.practicum.shareit.user.User;

import java.time.LocalDateTime;

/**
 * TODO Sprint add-item-requests.
 */
@Entity
@Table(name = "requests")
@Getter
@Setter
@ToString
@NoArgsConstructor
public class ItemRequest {

    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id; // уникальный идентификатор запроса;

    @Column(name = "description")
    private String description; // текст запроса, содержащий описание требуемой вещи;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "requestor_id")
    private User requestor; // пользователь, создавший запрос;

    @Column(name = "created")
    private LocalDateTime created; // дата и время создания запроса

    public ItemRequest(Long id) {
        this.id = id;
    }
}