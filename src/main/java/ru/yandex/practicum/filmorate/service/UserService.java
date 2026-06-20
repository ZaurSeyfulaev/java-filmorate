package ru.yandex.practicum.filmorate.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exceptions.NotFoundException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;

import java.util.Collection;
import java.util.Objects;
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

        User user = userStorage.getUserById(userId);
        User friend = userStorage.getUserById(friendId);
        validateUsersExist(user, friend);

        user.getFriends().add(friendId);
        friend.getFriends().add(userId);

        return userStorage.getUserById(friendId);
    }

    public User removeFriend(Long userId, Long friendId) {

        validateUserAndFriendIdsNotNull(userId, friendId);
        validateNotSameUser(userId, friendId, "Попытка удаления из друзей самого себя");

        User user = userStorage.getUserById(userId);
        User friend = userStorage.getUserById(friendId);
        validateUsersExist(user, friend);

        user.getFriends().remove(friendId);
        friend.getFriends().remove(userId);

        return userStorage.getUserById(friendId);
    }

    public Collection<User> getUserFriends(Long userId) {
        if (Objects.isNull(userId)) {
            log.error("Ошибка переданного значения userID = " + userId);
            throw new NotFoundException("Не передан идентификатор пользователя или друга");
        }
        User user = userStorage.getUserById(userId);
        if (user == null) {
            log.error("Пользователь не найден");
            throw new NotFoundException("Пользователь не найден");
        }
        return user.getFriends().stream()
                .map(userStorage::getUserById)
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
    }

    public Collection<User> getCommonFriends(Long userId, Long otherUserId) {
        validateUserAndFriendIdsNotNull(userId, otherUserId);
        validateNotSameUser(userId, otherUserId
                , "Попытка поиска общих друзей у с самим собой себя в друзья самого себя");

        User user = userStorage.getUserById(userId);
        User otherUser = userStorage.getUserById(otherUserId);
        validateUsersExist(user, otherUser);

        return user.getFriends().stream()
                .filter(otherUser.getFriends()::contains)
                .map(userStorage::getUserById)
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
    }

    private void validateUserAndFriendIdsNotNull(Long userId, Long friendId) {
        if (Objects.isNull(userId) || Objects.isNull(friendId)) {
            log.error("Ошибка переданного значения userID = {}, friendID = {}", userId, friendId);
            throw new NotFoundException("Ошибка переданного значения userID = " + userId + ", friendID = " + friendId);
        }
    }

    private void validateNotSameUser(Long userId, Long friendId, String message) {
        if (Objects.equals(userId, friendId)) {
            log.error(message);
            throw new NotFoundException(message);
        }
    }

    private void validateUsersExist(User user1, User user2) {
        if (user1 == null || user2 == null) {
            log.error("Один из пользователей не найден");
            throw new NotFoundException("Один из пользователей не найден");
        }
    }

}
