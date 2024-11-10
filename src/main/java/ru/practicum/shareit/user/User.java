package ru.practicum.shareit.user;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * TODO Sprint add-controllers.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class User {

    private Long id; // уникальный идентификатор пользователя;

    private String name; // имя или логин пользователя;

    private String email; // адрес электронной почты

}
