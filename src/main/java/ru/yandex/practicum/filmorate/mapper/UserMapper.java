package ru.yandex.practicum.filmorate.mapper;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import ru.yandex.practicum.filmorate.dto.userdto.UserDto;
import ru.yandex.practicum.filmorate.model.User;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class UserMapper {

    public static UserDto mapToUserDto(User user) {
        UserDto userDto = new UserDto();
        userDto.setId(user.getId());
        userDto.setName(user.getName());
        userDto.setEmail(user.getEmail());
        userDto.setLogin(user.getLogin());
        userDto.setBirthday(user.getBirthday());

        return userDto;
    }
}
