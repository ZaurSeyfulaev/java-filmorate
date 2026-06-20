package ru.yandex.practicum.filmorate.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.service.FilmService;
import ru.yandex.practicum.filmorate.storage.film.InMemoryFilmStorage;

import java.util.Collection;


@Slf4j
@RestController
@RequestMapping("/films")
public class FilmController {

    private final InMemoryFilmStorage filmStorage;
    private final FilmService filmService;

    public FilmController(InMemoryFilmStorage filmStorage, FilmService filmService) {
        this.filmStorage = filmStorage;
        this.filmService = filmService;
    }

    @GetMapping
    public Collection<Film> getFilms() {
        log.info("Вызван метод getFilms()");
        return filmStorage.getAllFilms();
    }

    @PostMapping
    public Film create(@RequestBody Film film) {
        log.info("Вызван метод createFilm()");
        return filmStorage.createFilm(film);
    }

    @PutMapping
    public Film update(@RequestBody Film newFilm) {
        log.info("Вызван метод updateFilm()");
        return filmStorage.updateFilm(newFilm);
    }

    @PutMapping("/{id}/like/{userId}")
    public Film likeFilm(@PathVariable Long id, @PathVariable Long userId) {
        log.info("Вызван метод addLike()");
        return filmService.addLike(userId, id);
    }

    @DeleteMapping("/{id}/like/{userId}")
    public Film deleteFilm(@PathVariable Long id, @PathVariable Long userId) {
        log.info("Вызван метод removeLike()");
        return filmService.removeLike(userId, id);
    }

    @GetMapping("/popular")
    public Collection<Film> getPopularFilms(@RequestParam(value = "count", defaultValue = "10") int count) {
        log.info("Вызван метод getTopFilm()");
        return filmService.getTopFilm(count);
    }
}

