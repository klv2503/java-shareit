package ru.practicum.shareit.user.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.practicum.shareit.auxiliary.validations.OnCreate;
import ru.practicum.shareit.auxiliary.validations.OnUpdate;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserDto {

    private Long id; // уникальный идентификатор пользователя;

    @NotBlank(groups = OnCreate.class)
    @Size(max = 200)
    private String name; // имя или логин пользователя;

    @NotBlank(groups = OnCreate.class)
    @Email(groups = {OnCreate.class, OnUpdate.class})
    @Size(max = 50)
    private String email; // адрес электронной почты

    public UserDto(String name, String email) {
        this.name = name;
        this.email = email;
    }
}
