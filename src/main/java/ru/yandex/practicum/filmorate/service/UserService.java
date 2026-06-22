package ru.yandex.practicum.filmorate.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exceptions.ConditionsNotMetException;
import ru.yandex.practicum.filmorate.exceptions.DuplicateDataException;
import ru.yandex.practicum.filmorate.exceptions.NotFoundException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;

import java.time.LocalDate;
import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

@Slf4j
@Service
public class UserService {
    private final UserStorage userStorage;

    public UserService(UserStorage userStorage) {
        this.userStorage = userStorage;
    }

    public User addFriend(Long userId, Long friendId) {

        validateUserAndFriendIdsNotNull(userId, friendId);
        validateNotSameUser(userId, friendId, "Попытка добавления в друзья самого себя");

        User user = validateUsersExist(userId);
        User friend = validateUsersExist(friendId);

        user.getFriends().add(friendId);
        friend.getFriends().add(userId);

        return friend;
    }

    public User removeFriend(Long userId, Long friendId) {

        validateUserAndFriendIdsNotNull(userId, friendId);
        validateNotSameUser(userId, friendId, "Попытка удаления из друзей самого себя");

        User user = validateUsersExist(userId);
        User friend = validateUsersExist(friendId);

        user.getFriends().remove(friendId);
        friend.getFriends().remove(userId);

        return friend;
    }

    public List<Optional<User>> getUserFriends(Long userId) {
        if (Objects.isNull(userId)) {
            throw new NotFoundException("Ошибка переданного значения userID = " + userId);
        }
        User user = validateUsersExist(userId);
        if (user == null) {
            throw new NotFoundException("Пользователь c id = " + userId + " не найден");
        }
        return user.getFriends().stream()
                .map(userStorage::getUserById)
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
    }

    public List<Optional<User>> getCommonFriends(Long userId, Long otherUserId) {
        validateUserAndFriendIdsNotNull(userId, otherUserId);
        validateNotSameUser(userId,
                otherUserId,
                "Попытка поиска общих друзей у с самим собой себя в друзья самого себя");

        User user = validateUsersExist(userId);
        User otherUser = validateUsersExist(otherUserId);

        return user.getFriends().stream()
                .filter(otherUser.getFriends()::contains)
                .map(userStorage::getUserById)
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
    }

    public Collection<User> getAllUsers() {
        return userStorage.getAllUsers();
    }

    public User createUser(User user) {
        validateEmail(user);
        checkEmailUnique(user.getEmail(), null);
        validateName(user);
        validateLogin(user);
        checkLoginUnique(user.getLogin(), null);
        validateBirthday(user);
        return userStorage.createUser(user);
    }

    public User updateUser(User newUser) {
        if (newUser.getId() == null) {
            throw new ConditionsNotMetException("Id должен быть указан");
        }
        validateUsersExist(newUser.getId());
        if (newUser.getName() != null) {
            validateName(newUser);
        }
        //Валидируем только переданные параметры
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
        return userStorage.updateUser(newUser);
    }

    private void validateUserAndFriendIdsNotNull(Long userId, Long friendId) {
        if (Objects.isNull(userId) || Objects.isNull(friendId)) {
            throw new NotFoundException("Ошибка переданного значения userID = " + userId + ", friendID = " + friendId);
        }
    }

    private void validateNotSameUser(Long userId, Long friendId, String message) {
        if (Objects.equals(userId, friendId)) {
            throw new NotFoundException(message);
        }
    }

    private void validateEmail(User user) {
        if (user.getEmail() == null || user.getEmail().isBlank() || !user.getEmail().contains("@")) {
            throw new ConditionsNotMetException("Имейл должен быть указан и содержать символ '@'");
        }
    }

    private void checkEmailUnique(String email, Long excludeUserId) {
        boolean emailExist = userStorage.getAllUsers().stream()
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
            throw new DuplicateDataException("Этот имейл уже используется");
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

    private User validateUsersExist(long userId) {
        return userStorage.getUserById(userId)
                .orElseThrow(() -> new NotFoundException("Пользователь с id: " + userId + "не найден"));

    }
}
