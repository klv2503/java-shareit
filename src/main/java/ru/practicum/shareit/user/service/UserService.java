package ru.practicum.shareit.user.service;

import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.dto.UserDto;

import java.util.List;

public interface UserService {

    UserDto createUser(UserDto userDto);

    UserDto updateUser(UserDto userDto);

    UserDto deleteUser(Long id);

    UserDto getUserById(Long id);

    List<UserDto> getAllUsers();

    User getUser(Long id);
}
