package ru.practicum.shareit.user;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.auxiliary.validations.NotEmptyUserDto;
import ru.practicum.shareit.auxiliary.validations.OnCreate;
import ru.practicum.shareit.auxiliary.validations.OnUpdate;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.service.UserService;

import java.util.List;

/**
 * TODO Sprint add-controllers.
 */
@Slf4j
@RestController
@RequestMapping(path = "/users")
@RequiredArgsConstructor
@Validated
public class UserController {

    private final UserService userService;

    @PostMapping
    public UserDto createUser(@Validated(OnCreate.class) @RequestBody UserDto userDto) {
        log.info("\nПолучен запрос на создание user {}", userDto);
        UserDto createdUser = userService.createUser(userDto);
        log.info("\nUser {} was created", createdUser);
        return createdUser;
    }

    @PatchMapping("/{userId}")
    public UserDto updateUser(@PathVariable(name = "userId") @NotNull @Positive Long id,
                              @Validated(OnUpdate.class) @NotEmptyUserDto @RequestBody UserDto userDto) {
        log.info("\nПолучен запрос на изменение данных {} user {}", userDto, id);
        userDto.setId(id);
        UserDto updatedUser = userService.updateUser(userDto);
        log.info("\nUser after updating {}", updatedUser);
        return updatedUser;
    }

    @DeleteMapping("/{id}")
    public UserDto deleteUser(@PathVariable @NotNull @Positive Long id) {
        log.info("\nПолучен запрос на удаление данных user {}", id);
        UserDto deletedUser = userService.deleteUser(id);
        log.info("\nWas deleted {}", deletedUser);
        return deletedUser;
    }

    @GetMapping("/{id}")
    public UserDto getUserById(@PathVariable @NotNull @Positive Long id) {
        log.info("\nПолучен запрос получение данных user {}", id);
        UserDto receivedUser = userService.getUserById(id);
        log.info("\nWas received {}", receivedUser);
        return receivedUser;
    }

    @GetMapping
    public List<UserDto> getAllUsers() {
        log.info("\nПолучен запрос получение всех user");
        List<UserDto> users = userService.getAllUsers();
        log.info("\nПолучен список из {} персон", users.size());
        return users;
    }
}
