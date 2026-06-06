package ru.yandex.practicum.filmorate.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exceptions.ConditionsNotMetException;
import ru.yandex.practicum.filmorate.exceptions.DuplicateDataException;
import ru.yandex.practicum.filmorate.exceptions.NotFoundException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.tools.GeneratorId;

import java.time.LocalDate;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/users")
public class UserController {

    private final Map<Long, User> users = new HashMap<>();

    @GetMapping
    public Collection<User> getUsers() {
        return users.values();
    }

    @PostMapping
    public User create(@RequestBody User user) {
        if (user.getEmail() == null || user.getEmail().isBlank() || !user.getEmail().contains("@")) {
            log.warn("Имейл должен быть указан и содержать символ '@'");
            throw new ConditionsNotMetException("Имейл должен быть указан и содержать символ '@'");
        }

        boolean emailExist = users.values().stream().anyMatch(u -> u.getEmail().equals(user.getEmail()));
        if (emailExist) {
            log.warn("Этот имейл уже используется");
            throw new DuplicateDataException("Этот имейл уже используется");
        }

        if (user.getLogin() == null || user.getLogin().isBlank() || user.getLogin().contains(" ")) {
            log.warn("login не должен быть пустым или содержать пробелы");
            throw new ConditionsNotMetException("login не должен быть пустым или содержать пробелы");
        }

        boolean loginExist = users.values().stream()
                .anyMatch(u -> u.getLogin().equals(user.getLogin()));
        if (loginExist) {
            log.warn("Этот логин уже используется");
            throw new DuplicateDataException("Этот логин уже используется");
        }

        if (user.getName() == null || user.getName().isBlank()) {
            log.info("Имя пользователя не передано. Будет присвоено имя  " + user.getLogin());
            user.setName(user.getLogin());
        }

        LocalDate now = LocalDate.now();
        if (user.getBirthday() == null || user.getBirthday().isAfter(now)) {
            log.warn("Дата рождения не может быть в будущем");
            throw new ConditionsNotMetException("Дата рождения не может быть в будущем");
        }

        user.setId(GeneratorId.generateId(users));
        log.info("Создан пользователь " + user.toString());
        users.put(user.getId(), user);
        return user;
    }

    @PutMapping
    public User update(@RequestBody User newUser) {
        if (newUser.getId() == null) {
            log.warn("Id должен быть указан");
            throw new ConditionsNotMetException("Id должен быть указан");
        }
        if (users.containsKey(newUser.getId())) {
            if (newUser.getName() == null || newUser.getName().isBlank()) {
                if (newUser.getLogin() != null) {
                    log.warn("Передали пустое имя -  подставляем login");
                    newUser.setName(newUser.getLogin());
                }
            }

            User oldUser = users.get(newUser.getId());
            if (newUser.getEmail() != null) {
                if (newUser.getEmail().isBlank() || !newUser.getEmail().contains("@")) {
                    log.warn("Имейл должен быть указан и содержать символ '@'");
                    throw new ConditionsNotMetException("Имейл должен быть указан и содержать символ '@'");
                }

                boolean emailExist = users.values().stream()
                        .filter(u -> !u.getId().equals(newUser.getId()))
                        .anyMatch(u -> u.getEmail().equals(newUser.getEmail()));
                if (emailExist) {
                    log.warn("Этот имейл уже используется");
                    throw new DuplicateDataException("Этот имейл уже используется");
                }
                log.info("Email изменен с " + oldUser.getEmail() + " на " + newUser.getEmail());
                oldUser.setEmail(newUser.getEmail());
            }

            if (newUser.getLogin() != null) {
                if (newUser.getLogin().isBlank() || newUser.getLogin().contains(" ")) {
                    log.warn("login не должен быть пустым или содержать пробелы");
                    throw new ConditionsNotMetException("login не должен быть пустым или содержать пробелы");
                }

                boolean loginExist = users.values().stream()
                        .filter(u -> !u.getId().equals(newUser.getId()))
                        .anyMatch(u -> u.getLogin().equals(newUser.getLogin()));
                if (loginExist) {
                    log.warn("Этот логин уже используется");
                    throw new DuplicateDataException("Этот логин уже используется");
                }
                log.info("Login изменен с " + oldUser.getLogin() + " на " + newUser.getLogin());
                oldUser.setLogin(newUser.getLogin());
            }

            if (newUser.getName() != null) {
                log.info("Имя изменено с " + oldUser.getName() + " на " + newUser.getName());
                oldUser.setName(newUser.getName());
            }

            LocalDate now = LocalDate.now();
            if (newUser.getBirthday() != null && newUser.getBirthday().isAfter(now)) {
                log.warn("Дата рождения не может быть в будущем");
                throw new ConditionsNotMetException("Дата рождения не может быть в будущем");
            } else if (newUser.getBirthday() != null) {
                log.info("Дата рождения изменена с " + oldUser.getBirthday() + " на " + newUser.getBirthday());
                oldUser.setBirthday(newUser.getBirthday());
            }
            log.info("Пользователь обновлен на " + oldUser.toString());
            return oldUser;
        }
        throw new NotFoundException("Пользователь с id = " + newUser.getId() + " не найден");
    }
}
