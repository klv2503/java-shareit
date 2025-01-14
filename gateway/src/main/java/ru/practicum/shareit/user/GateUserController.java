package ru.practicum.shareit.user;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.auxiliary.validations.NotEmptyUserDto;
import ru.practicum.shareit.auxiliary.validations.OnCreate;
import ru.practicum.shareit.auxiliary.validations.OnUpdate;
import ru.practicum.shareit.user.dto.UserDto;

@RestController
@RequestMapping(path = "/users")
@RequiredArgsConstructor
@Slf4j
@Validated
public class GateUserController {

    @Autowired
    private final UserClient userClient;

    @PostMapping
    public ResponseEntity<Object> createUser(@Validated(OnCreate.class) @RequestBody UserDto userDto) {
        log.info("\nGateway: Получен запрос на создание user {}", userDto);
        return userClient.createUser(userDto);
    }

    @PatchMapping("/{userId}")
    public ResponseEntity<Object> updateUser(@PathVariable(name = "userId") @NotNull @Positive Long id,
                                             @Validated(OnUpdate.class) @NotEmptyUserDto @RequestBody UserDto userDto) {
        log.info("\nGateway: Получен запрос на изменение данных {} user {}", userDto, id);
        return userClient.updateUser(id, userDto);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Object> deleteUser(@PathVariable @NotNull @Positive Long id) {
        log.info("\nGateway: Получен запрос на удаление данных user {}", id);
        return userClient.deleteUser(id);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Object> getUserById(@PathVariable @NotNull @Positive Long id) {
        log.info("\nGateway: Получен запрос получение данных user {}", id);
        return userClient.getUserById(id);
    }

    @GetMapping
    public ResponseEntity<Object> getAllUsers() {
        log.info("\nПолучен запрос получение всех user");
        return userClient.getAllUsers();
    }

}
