package ru.practicum.shareit.user.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserDto {

    private Long id; // уникальный идентификатор пользователя;

    private String name; // имя или логин пользователя;

    private String email; // адрес электронной почты

    public UserDto(String name, String email) {
        this.name = name;
        this.email = email;
    }
}
