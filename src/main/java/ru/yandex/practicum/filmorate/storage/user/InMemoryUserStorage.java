package ru.yandex.practicum.filmorate.storage.user;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.tools.GeneratorId;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Deprecated
@Slf4j
@Component
public class InMemoryUserStorage implements UserStorage {
    private final Map<Long, User> users = new HashMap<>();

    @Override
    public Collection<User> getAllUsers() {
        return users.values();
    }

    @Override
    public User createUser(User user) {
        user.setId(GeneratorId.generateId(users));
        log.info("Создан пользователь " + user.toString());
        users.put(user.getId(), user);
        return user;
    }

    @Override
    public User updateUser(User newUser) {

        User oldUser = users.get(newUser.getId());

        if (newUser.getEmail() != null) {
            log.info("Email изменен с " + oldUser.getEmail() + " на " + newUser.getEmail());
            oldUser.setEmail(newUser.getEmail());
        }

        if (newUser.getLogin() != null) {
            log.info("Login изменен с " + oldUser.getLogin() + " на " + newUser.getLogin());
            oldUser.setLogin(newUser.getLogin());
        }

        if (newUser.getName() != null) {
            log.info("Имя изменено с " + oldUser.getName() + " на " + newUser.getName());
            oldUser.setName(newUser.getName());
        }

        if (newUser.getBirthday() != null) {
            log.info("Дата рождения изменена с " + oldUser.getBirthday() + " на " + newUser.getBirthday());
            oldUser.setBirthday(newUser.getBirthday());
        }
        log.info("Пользователь обновлен на " + oldUser.toString());
        return oldUser;
    }

    @Override
    public Optional<User> getUserById(Long id) {
        return Optional.ofNullable(users.get(id));
    }
}
