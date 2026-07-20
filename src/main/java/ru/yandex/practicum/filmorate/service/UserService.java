package ru.yandex.practicum.filmorate.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.dao.user.UserDbStorage;
import ru.yandex.practicum.filmorate.dto.userdto.UserDto;
import ru.yandex.practicum.filmorate.exceptions.ConditionsNotMetException;
import ru.yandex.practicum.filmorate.exceptions.DuplicateDataException;
import ru.yandex.practicum.filmorate.exceptions.NotFoundException;
import ru.yandex.practicum.filmorate.mapper.UserMapper;
import ru.yandex.practicum.filmorate.model.User;

import java.time.LocalDate;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Slf4j
@Service
public class UserService {
    private final UserDbStorage userDbStorage;

    public UserService(UserDbStorage userDbStorage) {
        this.userDbStorage = userDbStorage;
    }

    public List<UserDto> getAllUsers() {
        return userDbStorage.getAllUsers().stream()
                .map(UserMapper::mapToUserDto)
                .collect(Collectors.toList());
    }

    public User createUser(User user) {
        validateEmail(user);
        checkEmailUnique(user.getEmail(), null);
        validateName(user);
        validateLogin(user);
        checkLoginUnique(user.getLogin(), null);
        validateBirthday(user);
        return userDbStorage.createUser(user);
    }

    public User updateUser(User newUser) {
        if (newUser.getId() == null) {
            throw new ConditionsNotMetException("Id должен быть указан");
        }

        validateUsersExist(newUser.getId());
        if (newUser.getName() != null) {
            validateName(newUser);
        }

        if (newUser.getEmail() != null) {
            validateEmail(newUser);
            checkEmailUnique(newUser.getEmail(), newUser.getId());
        }
        if (newUser.getLogin() != null) {
            validateLogin(newUser);
            checkLoginUnique(newUser.getLogin(), newUser.getId());
        }
        if (newUser.getName() != null) {
            validateName(newUser);
        }
        if (newUser.getBirthday() != null) {
            validateBirthday(newUser);
        }
        return userDbStorage.updateUser(newUser);
    }

    protected void validateUserAndFriendIdsNotNull(Long userId, Long friendId) {
        if (Objects.isNull(userId) || Objects.isNull(friendId)) {
            throw new NotFoundException("Ошибка переданного значения userID = " + userId + ", friendID = " + friendId);
        }
    }

    private void validateEmail(User user) {
        if (user.getEmail() == null || user.getEmail().isBlank() || !user.getEmail().contains("@")) {
            throw new ConditionsNotMetException("Имейл должен быть указан и содержать символ '@'");
        }
    }

    private void checkEmailUnique(String email, Long excludeUserId) {
        boolean emailExist = userDbStorage.getAllUsers().stream()
                .noneMatch(u -> (excludeUserId == null || !u.getId().equals(excludeUserId))
                        && u.getEmail().equals(email));
        if (!emailExist) {
            throw new DuplicateDataException("Этот имейл уже используется");
        }
    }

    private void validateLogin(User user) {
        if (user.getLogin() == null || user.getLogin().isBlank() || user.getLogin().contains(" ")) {
            throw new ConditionsNotMetException("login не должен быть пустым или содержать пробелы");
        }
    }

    private void checkLoginUnique(String login, Long excludeUserId) {
        boolean loginExist = getAllUsers().stream()
                .noneMatch(u -> (excludeUserId == null || !u.getId().equals(excludeUserId))
                        && u.getLogin().equals(login));
        if (!loginExist) {
            throw new DuplicateDataException("Этот login уже используется");
        }
    }

    private void validateName(User user) {
        if (user.getName() == null || user.getName().isBlank()) {
            log.info("Имя пользователя не передано. Будет присвоено имя  " + user.getLogin());
            user.setName(user.getLogin());
        }
    }

    private void validateBirthday(User user) {
        LocalDate now = LocalDate.now();
        if (user.getBirthday() == null || user.getBirthday().isAfter(now)) {
            throw new ConditionsNotMetException("Дата рождения не может быть в будущем");
        }
    }

    protected User validateUsersExist(long userId) {
        return userDbStorage.getUserById(userId)
                .orElseThrow(() -> new NotFoundException("Пользователь с id: " + userId + "не найден"));

    }
}
