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
import ru.yandex.practicum.filmorate.model.Genres;

import java.time.LocalDate;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Objects;

@Slf4j
@Service
public class FilmService {
    private final UserDbStorage userDbStorage;
    private final FilmDbStorage filmDbStorage;
    private final LikesDbStorage likesDbStorage;
    private final FilmGenreService filmGenreService;
    private final MpaService mpaService;

    public FilmService(UserDbStorage userDbStorage, FilmDbStorage filmDbStorage,
                       LikesDbStorage likesDbStorage, FilmGenreService filmGenreService, MpaService mpaService) {
        this.userDbStorage = userDbStorage;
        this.filmDbStorage = filmDbStorage;
        this.likesDbStorage = likesDbStorage;
        this.filmGenreService = filmGenreService;
        this.mpaService = mpaService;
    }

    public List<FilmDto> getFilms() {
        List<Film> films = filmDbStorage.getAllFilms();

        List<Film> filmsWithGenre = addGenreInFilm(films);

        return filmsWithGenre.stream()
                .map(FilmMapper::mapToFilmDto)
                .toList();
    }

    public Film getFilmById(Long id) {
        Film film = filmDbStorage.getFilmById(id).orElseThrow(()
                -> new NotFoundException("Фильм с id = " + id + " не найден"));
        List<Genres> genre = filmGenreService.getFilmGenreById(film.getId());
        film.setGenres(genre);
        return film;
    }

    public void addLike(Long userId, Long filmId) {
        validateIdsNotNull(userId, filmId);

        getFilmById(filmId);
        checkUserExists(userId);
        log.info("Попытка лайкнуть фильм");
        likesDbStorage.addLike(userId, filmId);
        log.info("Лайк добавлен");

    }

    public void removeLike(Long userId, Long filmId) {
        validateIdsNotNull(userId, filmId);
        getFilmById(filmId);
        checkUserExists(userId);
        log.info("Попытка удалить лайк");
        likesDbStorage.removeLike(userId, filmId);
        log.info("Лайк успешно удален");
    }

    public List<FilmDto> getTopFilm(int count) {
        if (count <= 0) {
            throw new ConditionsNotMetException("Значение count должно быть положительным числом");
        }
        List<Film> topFilms = filmDbStorage.getTopFilms(count);

        List<Film> topFilmsWithGenre = addGenreInFilm(topFilms);

        return topFilmsWithGenre.stream()
                .map(FilmMapper::mapToFilmDto)
                .toList();
    }

    public Film createFilm(Film film) {
        validateFilmName(film);
        validateFilmDescription(film);
        validateFilmReleaseDate(film);
        validateFilmDuration(film);
        validateMpa(film);
        Film createdFilm = filmDbStorage.createFilm(film);
        filmGenreService.addFilmGenre(createdFilm);
        return createdFilm;
    }

    public Film updateFilm(Film newFilm) {
        if (newFilm.getId() == null) {
            log.warn("ID должен быть указан");
            throw new ConditionsNotMetException("ID должен быть указан");
        }

        getFilmById(newFilm.getId());

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

    private List<Film> addGenreInFilm(List<Film> films) {

        List<Long> filmIds = films.stream().
                map(Film::getId).toList();

        Map<Long, List<Genres>> filmGenresMap = filmGenreService.getFilmGenresByIds(filmIds);

        films.forEach(film -> {
            List<Genres> genres = filmGenresMap.get(film.getId());
            film.setGenres(genres != null ? genres : Collections.emptyList());
        });
        return films;
    }

    private void checkUserExists(Long userId) {
        userDbStorage.getUserById(userId)
                .orElseThrow(() -> new NotFoundException("Пользователь c id = " + userId + " не найден"));
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

    private void validateMpa(Film film) {
        mpaService.getMpaId(film);
    }
}
