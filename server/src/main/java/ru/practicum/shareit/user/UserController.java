package ru.practicum.shareit.user;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
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
public class UserController {

    private final UserService userService;

    @PostMapping
    public ResponseEntity<UserDto> createUser(@RequestBody UserDto userDto) {
        log.info("\nПолучен запрос на создание user {}", userDto);
        UserDto createdUser = userService.createUser(userDto);
        log.info("\nUser {} was created", createdUser);
        return ResponseEntity.ok(createdUser);
    }

    @PatchMapping("/{userId}")
    public ResponseEntity<UserDto> updateUser(@PathVariable(name = "userId") Long id, @RequestBody UserDto userDto) {
        log.info("\nПолучен запрос на изменение данных {} user {}", userDto, id);
        userDto.setId(id);
        UserDto updatedUser = userService.updateUser(userDto);
        log.info("\nUser after updating {}", updatedUser);
        return ResponseEntity.ok(updatedUser);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<UserDto> deleteUser(@PathVariable Long id) {
        log.info("\nПолучен запрос на удаление данных user {}", id);
        UserDto deletedUser = userService.deleteUser(id);
        log.info("\nWas deleted {}", deletedUser);
        return ResponseEntity.ok(deletedUser);
    }

    @GetMapping("/{id}")
    public ResponseEntity<UserDto> getUserById(@PathVariable(name = "id") Long id) {
        log.info("\nПолучен запрос получение данных user {}", id);
        UserDto receivedUser = userService.getUserById(id);
        log.info("\nWas received {}", receivedUser);
        return ResponseEntity.ok(receivedUser);
    }

    @GetMapping
    public ResponseEntity<List<UserDto>> getAllUsers() {
        log.info("\nПолучен запрос получение всех user");
        List<UserDto> users = userService.getAllUsers();
        log.info("\nПолучен список из {} персон", users.size());
        return ResponseEntity.ok(users);
    }
}
