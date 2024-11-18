package ru.practicum.shareit.user.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.practicum.shareit.validations.OnCreate;
import ru.practicum.shareit.validations.OnUpdate;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserDto {

    private Long id; // уникальный идентификатор пользователя;

    @NotBlank(groups = OnCreate.class)
    private String name; // имя или логин пользователя;

    @NotBlank(groups = OnCreate.class)
    @Email(groups = {OnCreate.class, OnUpdate.class})
    private String email; // адрес электронной почты

}
