package ru.yandex.practicum.filmorate.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.dao.film.FilmDbStorage;
import ru.yandex.practicum.filmorate.dao.film.LikesDbStorage;
import ru.yandex.practicum.filmorate.dao.user.UserDbStorage;
import ru.yandex.practicum.filmorate.dto.filmdto.FilmDto;
import ru.yandex.practicum.filmorate.exceptions.ConditionsNotMetException;
import ru.yandex.practicum.filmorate.exceptions.NotFoundException;
import ru.yandex.practicum.filmorate.mapper.FilmMapper;
import ru.yandex.practicum.filmorate.model.Film;

import java.time.LocalDate;
import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Slf4j
@Service
public class FilmService {
    private final UserDbStorage userDbStorage;
    private final FilmDbStorage filmDbStorage;
    private final LikesDbStorage likesDbStorage;

    public FilmService(UserDbStorage userDbStorage, FilmDbStorage filmDbStorage, LikesDbStorage likesDbStorage) {
        this.userDbStorage = userDbStorage;
        this.filmDbStorage = filmDbStorage;
        this.likesDbStorage = likesDbStorage;
    }

    public List<FilmDto> getFilms() {
        return filmDbStorage.getAllFilms()
                .stream()
                .map(FilmMapper::mapToFilmDto)
                .collect(Collectors.toList());
    }

    public Film getFilmById(Long id) {
        return filmDbStorage.getFilmById(id).orElseThrow(()
                -> new NotFoundException("Фильм с id = " + id + " не найден"));
    }

    public void addLike(Long userId, Long filmId) {
        validateIdsNotNull(userId, filmId);

        filmDbStorage.getFilmById(filmId)
                .orElseThrow(() -> new NotFoundException("Фильм с id = " + filmId + " не найден"));

        log.info("Попытка лайкнуть фильм");
        userDbStorage.getUserById(userId)
                .orElseThrow(() -> new NotFoundException("Пользователь c id = " + userId + " не найден"));
        likesDbStorage.addLike(userId, filmId);
        log.info("Лайк добавлен");

    }

    public void removeLike(Long userId, Long filmId) {
        validateIdsNotNull(userId, filmId);
        filmDbStorage.getFilmById(filmId)
                .orElseThrow(() -> new NotFoundException("Фильм с id = " + filmId + " не найден"));
        userDbStorage.getUserById(userId)
                .orElseThrow(() -> new NotFoundException("Пользователь c id = " + userId + " не найден"));

        log.info("Попытка удалить лайк");
        likesDbStorage.removeLike(userId, filmId);
        log.info("Лайк успешно удален");
    }

    public Collection<Film> getTopFilm(int count) {
        if (count <= 0) {
            throw new ConditionsNotMetException("Значение count должно быть положительным числом");
        }
        return filmDbStorage.getTopFilms(count);
    }

    public Film createFilm(Film film) {
        validateFilmName(film);
        validateFilmDescription(film);
        validateFilmReleaseDate(film);
        validateFilmDuration(film);
        return filmDbStorage.createFilm(film);
    }

    public Film updateFilm(Film newFilm) {
        if (newFilm.getId() == null) {
            log.warn("ID должен быть указан");
            throw new ConditionsNotMetException("ID должен быть указан");
        }
        filmDbStorage.getFilmById(newFilm.getId())
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
        return filmDbStorage.updateFilm(newFilm);
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
