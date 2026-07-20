package ru.yandex.practicum.filmorate.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.dto.userdto.UserDto;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.service.FriendshipService;
import ru.yandex.practicum.filmorate.service.UserService;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/users")
public class UserController {
    private final UserService userService;
    private final FriendshipService friendshipService;

    UserController(UserService userService, FriendshipService friendshipService) {
        this.userService = userService;
        this.friendshipService = friendshipService;
    }

    @GetMapping
    public List<UserDto> getUsers() { // готово
        log.info("Вызван метод getUsers()");
        return userService.getAllUsers();
    }

    @PostMapping
    public User create(@RequestBody User user) { //готово
        log.info("Вызван метод createUser для создания нового пользователя");

        return userService.createUser(user);
    }

    @PutMapping
    public User update(@RequestBody User newUser) {
        log.info("Вызван метод updateUser для создания нового пользователя");
        return userService.updateUser(newUser);
    }

    @PutMapping("/{id}/friends/{friendId}")
    public void addFriend(@PathVariable Long id, @PathVariable Long friendId) {
        log.info("Вызываем метод добавления друзей");
        log.info("Пользователь с ID= " + id + " добавляет в друзья пользователя с ID =  " + friendId);
        friendshipService.addFriend(id, friendId);
    }

    @DeleteMapping("/{id}/friends/{friendId}")
    public void deleteFriend(@PathVariable Long id, @PathVariable Long friendId) {
        log.info("Вызываем метод удаления друзей");
        log.info("Пользователь с ID= " + id + " удаляет из друзей пользователя с ID =  " + friendId);
        friendshipService.deleteFriend(id, friendId);
    }

    @GetMapping("/{id}/friends")
    public List<User> getFriends(@PathVariable Long id) {
        log.info("Получаем список друзей пользователя с ID = " + id);
        return friendshipService.getAllFriendships(id);
    }

    @GetMapping("/{id}/friends/common/{otherId}")
    public List<User> getCommonFriends(@PathVariable Long id, @PathVariable Long otherId) {
        log.info("Получаем список общих друзей");
        return friendshipService.getCommonFriends(id, otherId);
    }
}
