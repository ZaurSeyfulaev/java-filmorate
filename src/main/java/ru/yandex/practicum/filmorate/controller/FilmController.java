package ru.yandex.practicum.filmorate.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exceptions.ConditionsNotMetException;
import ru.yandex.practicum.filmorate.exceptions.NotFoundException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.tools.GeneratorId;

import java.time.LocalDate;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/films")
public class FilmController {

    private final Map<Long, Film> films = new HashMap<>();

    @GetMapping
    public Collection<Film> getFilms() {
        log.info("Вызван метод getFilms()");
        return films.values();
    }

    @PostMapping
    public Film create(@RequestBody Film film) {

        validateFilmName(film);
        validateFilmDescription(film);
        validateFilmReleaseDate(film);
        validateFilmDuration(film);

        film.setId(GeneratorId.generateId(films));
        films.put(film.getId(), film);
        log.info("Добавлен фильм: " + film.toString());
        return film;
    }

    @PutMapping
    public Film update(@RequestBody Film newFilm) {

        if (newFilm.getId() == null) {
            log.warn("ID должен быть указан");
            throw new ConditionsNotMetException("ID должен быть указан");
        }

        if (films.containsKey(newFilm.getId())) {
            Film oldFilm = films.get(newFilm.getId());
            //Название
            if (newFilm.getName() != null && !newFilm.getName().isBlank()) {
                log.info("Название фильма изменено с " + oldFilm.getName() + " на " + newFilm.getName());
                oldFilm.setName(newFilm.getName());
            }
            //Описание
            if (newFilm.getDescription() != null && !newFilm.getDescription().isBlank()) {
                validateFilmDescription(newFilm);

                log.info("Описание фильма изменено с " + oldFilm.getDescription() + " на " + newFilm.getDescription());
                oldFilm.setDescription(newFilm.getDescription());
            }
            // Дата релиза
            if (newFilm.getReleaseDate() != null) {
                validateFilmReleaseDate(newFilm);

                log.info("Дата релиза фильма изменена с " + oldFilm.getReleaseDate() + " на " + newFilm.getReleaseDate());
                oldFilm.setReleaseDate(newFilm.getReleaseDate());
            }
            //Продолжительность\
            if (newFilm.getDuration() != null) {
                validateFilmDuration(newFilm);

                log.info("Длительность фильма изменена с " + oldFilm.getDuration() + " на " + newFilm.getDuration());
                oldFilm.setDuration(newFilm.getDuration());

            }
            return oldFilm;
        }

        log.warn("Фильм с id = " + newFilm.getId() + " не найден");
        throw new NotFoundException("Фильм с id = " + newFilm.getId() + " не найден");

    }

    private void validateFilmName(Film film) {
        if (film.getName() == null || film.getName().isBlank()) {
            log.warn("Название фильма не может быть пустым");
            throw new ConditionsNotMetException("Название фильма не может быть пустым");
        }
    }

    private void validateFilmDescription(Film film) {
        int maxDescriptionLength = 200;
        if (film.getDescription() != null) {
            if (film.getDescription().length() > maxDescriptionLength) {
                log.warn("Описание не должно быть больше 200 символов");
                throw new ConditionsNotMetException("Описание не должно быть больше 200 символов");
            }
        }
    }

    private void validateFilmReleaseDate(Film film) {
        LocalDate minReleaseDate = LocalDate.of(1895, 12, 28);
        if (film.getReleaseDate() == null) {
            log.warn("Дата релиза не может быть пустой");
            throw new ConditionsNotMetException("Дата релиза не может быть пустой");
        }
        if (film.getReleaseDate().isBefore(minReleaseDate)) {
            log.warn("Дата релиза фильма не может быть раньше 28 декабря 1895 года");
            throw new ConditionsNotMetException("Дата релиза фильма не может быть раньше 28 декабря 1895 года");
        }
    }

    private void validateFilmDuration(Film film) {
        if (film.getDuration() == null) {
            log.warn("Не передана длительность фильма");
            throw new ConditionsNotMetException("Не передана длительность фильма");
        }
        if (film.getDuration() <= 0) {
            log.warn("Длительность должна быть положительной");
            throw new ConditionsNotMetException("Длительность должна быть положительной");
        }
    }
}

