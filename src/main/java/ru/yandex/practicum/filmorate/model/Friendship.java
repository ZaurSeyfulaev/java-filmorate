package ru.yandex.practicum.filmorate.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import ru.yandex.practicum.filmorate.enums.FriendStatus;


@Data
@AllArgsConstructor
public class Friendship {
    private Long userId;
    private Long friendId;
    private FriendStatus status;
}
