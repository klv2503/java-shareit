package ru.practicum.shareit.user.dto;

import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;
import ru.practicum.shareit.user.User;

import java.util.List;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface UserMapper {

    User mapUserDtoToUser(UserDto userDto);

    UserDto mapUserToDto(User user);

    List<User> mapDtoListToUsersList(List<UserDto> userDtos);

    List<UserDto> mapUsersListToDtoList(List<User> users);

}
