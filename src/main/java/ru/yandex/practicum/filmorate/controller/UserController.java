package ru.yandex.practicum.filmorate.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.service.UserService;
import ru.yandex.practicum.filmorate.storage.user.InMemoryUserStorage;

import java.util.Collection;

@Slf4j
@RestController
@RequestMapping("/users")
public class UserController {
    private final InMemoryUserStorage inMemoryUserStorage;
    private final UserService userService;

    UserController(InMemoryUserStorage inMemoryUserStorage, UserService userService) {
        this.inMemoryUserStorage = inMemoryUserStorage;
        this.userService = userService;
    }

    @GetMapping
    public Collection<User> getUsers() {
        log.info("Вызван метод getUsers()");
        return inMemoryUserStorage.getAllUsers();
    }

    @PostMapping
    public User create(@RequestBody User user) {
        log.info("Вызван метод createUser для создания нового пользователя");
        return inMemoryUserStorage.createUser(user);
    }

    @PutMapping
    public User update(@RequestBody User newUser) {
        log.info("Вызван метод updateUser для создания нового пользователя");
        return inMemoryUserStorage.updateUser(newUser);
    }

    @PutMapping("/{id}/friends/{friendId}")
    public User addFriend(@PathVariable Long id, @PathVariable Long friendId) {
        log.info("Вызываем метод добавления друзей");
        return userService.addFriend(id, friendId);
    }

    @DeleteMapping("/{id}/friends/{friendId}")
    public User deleteFriend(@PathVariable Long id, @PathVariable Long friendId) {
        log.info("Вызываем метод удаления друзей");
        return userService.removeFriend(id, friendId);
    }

    @GetMapping("/{id}/friends")
    public Collection<User> getFriends(@PathVariable Long id) {
        log.info("Получаем список друзей");
        return userService.getUserFriends(id);
    }

    @GetMapping("/{id}/friends/common/{otherId}")
    public Collection<User> getCommonFriends(@PathVariable Long id, @PathVariable Long otherId) {
        log.info("Получаем список общих друзей");
        return userService.getCommonFriends(id, otherId);
    }
}
