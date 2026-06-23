package ru.yandex.practicum.filmorate.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.service.UserService;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

@Slf4j
@RestController
@RequestMapping("/users")
public class UserController {
    private final UserService userService;

    UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping
    public Collection<User> getUsers() {
        log.info("Вызван метод getUsers()");
        return userService.getAllUsers();
    }

    @PostMapping
    public User create(@RequestBody User user) {
        log.info("Вызван метод createUser для создания нового пользователя");
        return userService.createUser(user);
    }

    @PutMapping
    public User update(@RequestBody User newUser) {
        log.info("Вызван метод updateUser для создания нового пользователя");
        return userService.updateUser(newUser);
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
    public List<Optional<User>> getFriends(@PathVariable Long id) {
        log.info("Получаем список друзей");
        return userService.getUserFriends(id);
    }

    @GetMapping("/{id}/friends/common/{otherId}")
    public List<Optional<User>> getCommonFriends(@PathVariable Long id, @PathVariable Long otherId) {
        log.info("Получаем список общих друзей");
        return userService.getCommonFriends(id, otherId);
    }
}
