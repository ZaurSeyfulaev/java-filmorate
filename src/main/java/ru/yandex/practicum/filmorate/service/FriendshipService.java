package ru.yandex.practicum.filmorate.service;

import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.enums.FriendStatus;

import java.util.Set;

@Service
public class FriendshipService {

    //Оставляю заявку в друзья По умолчанию false
    public void addFriend(Long userId, Long friendId) {

    }

    // Подтверждаем дружбу - меняем статус на true
    public void confirmFriend(Long userId, Long friendId) {

    }

    // запрос к БД возвращаю значение enum в зависимости от значения в БД
    public FriendStatus getStatus(Long userId, Long friendId) {
        return null;
    }

    // Верну всех друзей со статусом true
    public Set<Long> getConfirmedFriends(Long userId) {
        return null;
    }
}
