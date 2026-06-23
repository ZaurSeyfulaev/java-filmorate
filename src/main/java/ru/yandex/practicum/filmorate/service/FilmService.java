package ru.yandex.practicum.filmorate.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exceptions.ConditionsNotMetException;
import ru.yandex.practicum.filmorate.exceptions.NotFoundException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.storage.film.FilmStorage;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;

import java.time.LocalDate;
import java.util.Collection;
import java.util.Objects;

@Slf4j
@Service
public class FilmService {
    private final FilmStorage filmStorage;
    private final UserStorage userStorage;

    public FilmService(FilmStorage filmStorage, UserStorage userStorage) {
        this.filmStorage = filmStorage;
        this.userStorage = userStorage;
    }

    public Film addLike(Long userId, Long filmId) {
        validateIdsNotNull(userId, filmId);

        Film film = filmStorage.getFilmById(filmId)
                .orElseThrow(() -> new NotFoundException("Фильм с id = " + filmId + " не найден"));
        userStorage.getUserById(userId);
        log.info("Попытка лайкнуть фильм");
        userStorage.getUserById(userId)
                .orElseThrow(() -> new NotFoundException("Пользователь c id = " + userId + " не найден"));
        film.getLikes().add(userId);
        log.info("Лайк добавлен. Количество лайков = {}", film.getLikes().size());

        return film;
    }

    public Film removeLike(Long userId, Long filmId) {
        validateIdsNotNull(userId, filmId);
        Film film = filmStorage.getFilmById(filmId)
                .orElseThrow(() -> new NotFoundException("Фильм с id = " + filmId + " не найден"));
        userStorage.getUserById(userId)
                .orElseThrow(() -> new NotFoundException("Пользователь c id = " + userId + " не найден"));

        log.info("Попытка удалить лайк");
        film.getLikes().remove(userId);
        log.info("Лайк успешно удален. Общее количество лайков => {}", film.getLikes().size());
        return film;
    }

    public Collection<Film> getTopFilm(int count) {
        if (count <= 0) {
            throw new ConditionsNotMetException("Значение count должно быть положительным числом");
        }
        return filmStorage.getTopFilm(count);
    }

    public Collection<Film> getAllFilms() {
        return filmStorage.getAllFilms();
    }

    public Film createFilm(Film film) {
        validateFilmName(film);
        validateFilmDescription(film);
        validateFilmReleaseDate(film);
        validateFilmDuration(film);
        return filmStorage.createFilm(film);
    }

    public Film updateFilm(Film newFilm) {
        if (newFilm.getId() == null) {
            log.warn("ID должен быть указан");
            throw new ConditionsNotMetException("ID должен быть указан");
        }
        filmStorage.getFilmById(newFilm.getId())
                .orElseThrow(() -> new NotFoundException("Фильм с id = " + newFilm.getId() + " не найден"));

        if (newFilm.getName() != null) {
            validateFilmName(newFilm);
        }

        if (newFilm.getDescription() != null) {
            validateFilmDescription(newFilm);
        }
        if (newFilm.getReleaseDate() != null) {
            validateFilmReleaseDate(newFilm);
        }
        if (newFilm.getDuration() != null) {
            validateFilmDuration(newFilm);
        }
        return filmStorage.updateFilm(newFilm);
    }

    private void validateIdsNotNull(Long userId, Long filmId) {
        if (Objects.isNull(userId) || Objects.isNull(filmId)) {
            throw new NotFoundException("Ошибка переданного значения userId = " + userId + ", filmId = " + filmId);
        }
    }

    private void validateFilmName(Film film) {
        if (film.getName() == null || film.getName().isBlank()) {
            throw new ConditionsNotMetException("Название фильма не может быть пустым");
        }
    }

    private void validateFilmDescription(Film film) {
        int maxDescriptionLength = 200;
        if (film.getDescription() != null) {
            if (film.getDescription().length() > maxDescriptionLength) {
                throw new ConditionsNotMetException("Описание не должно быть больше 200 символов");
            }
        }
    }

    private void validateFilmReleaseDate(Film film) {
        LocalDate minReleaseDate = LocalDate.of(1895, 12, 28);
        if (film.getReleaseDate() == null) {
            throw new ConditionsNotMetException("Дата релиза не может быть пустой");
        }
        if (film.getReleaseDate().isBefore(minReleaseDate)) {
            throw new ConditionsNotMetException("Дата релиза фильма не может быть раньше 28 декабря 1895 года");
        }
    }

    private void validateFilmDuration(Film film) {
        if (film.getDuration() == null) {
            throw new ConditionsNotMetException("Не передана длительность фильма");
        }
        if (film.getDuration() <= 0) {
            throw new ConditionsNotMetException("Длительность должна быть положительной");
        }
    }
}
