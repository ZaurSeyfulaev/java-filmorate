package ru.yandex.practicum.filmorate.service;

import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.dao.user.FriendshipDbStorage;
import ru.yandex.practicum.filmorate.exceptions.ConditionsNotMetException;
import ru.yandex.practicum.filmorate.exceptions.DuplicateDataException;
import ru.yandex.practicum.filmorate.exceptions.NotFoundException;
import ru.yandex.practicum.filmorate.model.Friendship;
import ru.yandex.practicum.filmorate.model.User;

import java.util.List;

@Service
public class FriendshipService {
    private final FriendshipDbStorage friendshipDbStorage;
    private final UserService userService;

    public FriendshipService(FriendshipDbStorage friendshipDbStorage, UserService userService) {
        this.friendshipDbStorage = friendshipDbStorage;
        this.userService = userService;
    }

    public void addFriend(Long userId, Long friendId) {
        userService.validateUserAndFriendIdsNotNull(userId, friendId);
        userService.validateUsersExist(userId);
        userService.validateUsersExist(friendId);

        List<Friendship> friendshipList = friendshipDbStorage.getFriendship(userId, friendId);

        if (friendshipList.size() > 0) {
            throw new DuplicateDataException("Заявка в друзья уже отправлена");
        }

        friendshipDbStorage.addFriendship(userId, friendId);
    }

    public void deleteFriend(Long userId, Long friendId) {
        userService.validateUserAndFriendIdsNotNull(userId, friendId);
        userService.validateUsersExist(userId);
        userService.validateUsersExist(friendId);

        friendshipDbStorage.deleteFriendship(userId, friendId);
    }

    public List<User> getAllFriendships(Long userId) {
        if (userId == null) {
            throw new ConditionsNotMetException("id не может быть null");
        }

        userService.validateUsersExist(userId);

        List<User> friendList = friendshipDbStorage.getAllUserFriends(userId);

        return friendList;
    }

    public List<User> getCommonFriends(Long user1, Long user2) {
        userService.validateUserAndFriendIdsNotNull(user1, user2);
        userService.validateUsersExist(user1);
        userService.validateUsersExist(user2);
        List<User> commonFriendList = friendshipDbStorage.getCommonUserFriends(user1, user2);

        if (commonFriendList.isEmpty()) {
            throw new NotFoundException("У пользователей нет общих друзей");
        }

        return commonFriendList;
    }
}
