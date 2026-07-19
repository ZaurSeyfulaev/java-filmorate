package ru.yandex.practicum.filmorate.enums;

import lombok.Getter;

@Getter
public enum FriendStatus {
    UNCONFIRMED(1),
    CONFIRMED(2);

    private int id;

    FriendStatus(int id) {
        this.id = id;
    }
}
