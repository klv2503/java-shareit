package ru.practicum.shareit.user.service;

import lombok.RequiredArgsConstructor;
import org.apache.logging.log4j.util.Strings;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.auxiliary.exceptions.DuplicateDataException;
import ru.practicum.shareit.auxiliary.exceptions.NotFoundException;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserRepository;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.dto.UserMapper;

import java.util.List;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserMapper userMapper;
    private final UserRepository repository;

    @Override
    public UserDto createUser(UserDto userDto) {
        try {
            return userMapper.mapUserToDto(repository.save(userMapper.mapUserDtoToUser(userDto)));
        } catch (DataIntegrityViolationException e) {
            throw new DuplicateDataException("Users e-mail already exists in base", userDto);
        }
    }

    @Override
    public UserDto updateUser(UserDto userDto) {
        User oldUser = getUser(userDto.getId());
        if (!Strings.isBlank(userDto.getName()))
            oldUser.setName(userDto.getName());
        if (!Strings.isBlank(userDto.getEmail()))
            oldUser.setEmail(userDto.getEmail());
        try {
            return userMapper.mapUserToDto(repository.save(oldUser));
        } catch (DataIntegrityViolationException e) {
            throw new DuplicateDataException("This e-mail already exists in base", userDto);
        }
    }

    @Override
    public UserDto deleteUser(Long id) {
        User oldUser = getUser(id);
        repository.deleteById(id);
        return userMapper.mapUserToDto(oldUser);
    }

    @Override
    public UserDto getUserById(Long id) {
        User user = getUser(id);
        return userMapper.mapUserToDto(user);
    }

    @Override
    public List<UserDto> getAllUsers() {
        return userMapper.mapUsersListToDtoList(repository.findAll());
    }

    public User getUser(Long usersId) {
        return repository.findById(usersId)
                .orElseThrow(() -> new NotFoundException("User not found", usersId));
    }
}
