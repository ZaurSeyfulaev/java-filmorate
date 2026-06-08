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
        log.info("Вызван метод getUsers()");
        return users.values();
    }

    @PostMapping
    public User create(@RequestBody User user) {

        validateEmail(user);
        checkEmailUnique(user.getEmail(), null);
        validateName(user);
        validateLogin(user);
        checkLoginUnique(user.getLogin(), null);
        validateBirthday(user);

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
            validateName(newUser);
            User oldUser = users.get(newUser.getId());

            if (newUser.getEmail() != null) {
                validateEmail(newUser);
                checkEmailUnique(newUser.getEmail(), newUser.getId());

                log.info("Email изменен с " + oldUser.getEmail() + " на " + newUser.getEmail());
                oldUser.setEmail(newUser.getEmail());
            }

            if (newUser.getLogin() != null) {
                validateLogin(newUser);
                checkLoginUnique(newUser.getLogin(), newUser.getId());

                log.info("Login изменен с " + oldUser.getLogin() + " на " + newUser.getLogin());
                oldUser.setLogin(newUser.getLogin());
            }

            if (newUser.getName() != null) {
                validateName(newUser);
                log.info("Имя изменено с " + oldUser.getName() + " на " + newUser.getName());
                oldUser.setName(newUser.getName());
            }

            if (newUser.getBirthday() != null) {
                validateBirthday(newUser);

                log.info("Дата рождения изменена с " + oldUser.getBirthday() + " на " + newUser.getBirthday());
                oldUser.setBirthday(newUser.getBirthday());
            }
            log.info("Пользователь обновлен на " + oldUser.toString());
            return oldUser;
        }
        throw new NotFoundException("Пользователь с id = " + newUser.getId() + " не найден");
    }
// Методы валидаторы

    private void validateEmail(User user) {
        if (user.getEmail() == null || user.getEmail().isBlank() || !user.getEmail().contains("@")) {
            log.warn("Имейл должен быть указан и содержать символ '@'");
            throw new ConditionsNotMetException("Имейл должен быть указан и содержать символ '@'");
        }
    }

    private void checkEmailUnique(String email, Long excludeUserId) {
        boolean emailExist = users.values().stream()
                .noneMatch(u -> (excludeUserId == null || !u.getId().equals(excludeUserId))
                        && u.getEmail().equals(email));
        if (!emailExist) {
            log.warn("Этот имейл уже используется");
            throw new DuplicateDataException("Этот имейл уже используется");
        }
    }

    private void validateLogin(User user) {
        if (user.getLogin() == null || user.getLogin().isBlank() || user.getLogin().contains(" ")) {
            log.warn("login не должен быть пустым или содержать пробелы");
            throw new ConditionsNotMetException("login не должен быть пустым или содержать пробелы");
        }
    }

    private void checkLoginUnique(String login, Long excludeUserId) {
        boolean loginExist = users.values().stream()
                .noneMatch(u -> (excludeUserId == null || !u.getId().equals(excludeUserId))
                        && u.getLogin().equals(login));
        if (!loginExist) {
            log.warn("Этот имейл уже используется");
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
            log.warn("Дата рождения не может быть в будущем");
            throw new ConditionsNotMetException("Дата рождения не может быть в будущем");
        }
    }
}
