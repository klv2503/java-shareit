package ru.practicum.shareit.user;

import jakarta.persistence.*;
import lombok.*;

/**
 * TODO Sprint add-controllers.
 */

@Entity
@Table(name = "users")
@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class User {

    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id; // уникальный идентификатор пользователя;

    @Column(name = "name")
    private String name; // имя или логин пользователя;

    @Column(name = "email")
    private String email; // адрес электронной почты

    public User(Long id) {
        this.id = id;
    }
}
