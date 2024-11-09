package ru.practicum.shareit.user.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exceptions.DuplicateDataException;
import ru.practicum.shareit.exceptions.NotFoundException;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserMemoryStorage;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.dto.UserMapper;

import java.util.List;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserMemoryStorage userStorage;
    private final UserMapper userMapper;

    @Override
    public UserDto createUser(UserDto userDto) {
        if (userStorage.isEmailExists(userDto.getEmail()))
            throw new DuplicateDataException("Users e-mail already exists in base", userDto);
        return userMapper.mapUserToDto(userStorage.addNewUser(userMapper.mapUserDtoToUser(userDto)));
    }

    @Override
    public UserDto updateUser(UserDto userDto) {
        User oldUser = userStorage.getUserById(userDto.getId());
        if (oldUser == null)
            throw new NotFoundException("User not found", userDto);
        if (userDto.getName() != null && !userDto.getName().isBlank())
            oldUser.setName(userDto.getName());
        if (userDto.getEmail() != null && !userDto.getEmail().isBlank())
            oldUser.setEmail(userDto.getEmail());
        if (userStorage.isEmailExists(oldUser.getId(), oldUser.getEmail()))
            throw new DuplicateDataException("This e-mail already exists in base", userDto);
        return userMapper.mapUserToDto(userStorage.updateUser(oldUser));
    }

    @Override
    public UserDto deleteUser(Long id) {
        if (userStorage.getUserById(id) == null)
            throw new NotFoundException("Not found user id = " + id, id);
        return userMapper.mapUserToDto(userStorage.deleteUser(id));
    }

    @Override
    public UserDto getUserById(Long id) {
        User user = userStorage.getUserById(id);
        if (user == null)
            throw new NotFoundException("Not found user id = " + id, id);
        return userMapper.mapUserToDto(user);
    }

    @Override
    public List<UserDto> getAllUsers() {
        return userMapper.mapUsersListToDtoList(userStorage.getAllUsers());
    }
}
