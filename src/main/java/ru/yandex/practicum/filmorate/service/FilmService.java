package ru.yandex.practicum.filmorate.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exceptions.NotFoundException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.storage.film.FilmStorage;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;

import java.util.Collection;
import java.util.Comparator;
import java.util.Objects;
import java.util.stream.Collectors;

@Slf4j
@Service
public class FilmService {
    private final FilmStorage filmStorage;
    private final UserStorage userStorage;

    public FilmService(FilmStorage filmStorage, UserStorage userStorage) {
        this.filmStorage = filmStorage;
        this.userStorage = userStorage;
    }

    public static void validateIdsNotNull(Long userId, Long filmId) {
        if (Objects.isNull(userId) || Objects.isNull(filmId)) {
            log.error("Ошибка переданного значения id1 = " + userId + ", id2 = " + filmId);
            throw new NotFoundException("Ошибка переданного значения id1 = " + userId + ", id2 = " + filmId);
        }
    }

    public static void validateNotSameIds(Long userId, Long filmId, String message) {
        if (Objects.equals(userId, filmId)) {
            log.error(message);
            throw new NotFoundException(message);
        }
    }

    public Film addLike(Long userId, Long filmId) {
        validateIdsNotNull(userId, filmId);
        validateNotSameIds(userId, filmId, "Переданы одинаковые идентификаторы");

        Film film = filmStorage.getFilmById(filmId);
        userStorage.getUserById(userId);
        if (film == null) {
            log.error("Фильм не найден");
            throw new NotFoundException("Фильм не найден");
        }
        log.info("Попытка лайкнуть фильм");
        film.getLikes().add(userId);
        log.info("Лайк успешно добавлен. Общее количество лайков => {}", film.getLikes().size());

        return film;
    }

    public Film removeLike(Long userId, Long filmId) {
        validateIdsNotNull(userId, filmId);
        validateNotSameIds(userId, filmId, "Переданы одинаковые идентификаторы");

        Film film = filmStorage.getFilmById(filmId);
        userStorage.getUserById(userId);
        if (film == null) {
            log.error("Фильм не найден");
            throw new NotFoundException("Фильм не найден");
        }
        log.info("Попытка удалить лайк");
        film.getLikes().remove(userId);
        log.info("Лайк успешно удален. Общее количество лайков => {}", film.getLikes().size());

        return film;
    }

    public Collection<Film> getTopFilm(int count) {
        return filmStorage.getAllFilms().stream()
                .sorted(Comparator.comparingInt((Film film) -> film.getLikes().size()).reversed())
                .limit(count)
                .collect(Collectors.toList());
    }
}
