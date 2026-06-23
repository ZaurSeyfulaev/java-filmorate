package ru.yandex.practicum.filmorate.storage.film;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exceptions.NotFoundException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.tools.GeneratorId;

import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Component
public class InMemoryFilmStorage implements FilmStorage {

    private final Map<Long, Film> films = new HashMap<>();

    @Override
    public Film createFilm(Film film) {
        film.setId(GeneratorId.generateId(films));
        films.put(film.getId(), film);
        log.info("Добавлен фильм: " + film.toString());
        return film;
    }

    @Override
    public Film updateFilm(Film newFilm) {

        if (films.containsKey(newFilm.getId())) {
            Film oldFilm = films.get(newFilm.getId());
            //Название
            if (newFilm.getName() != null) {
                log.info("Название фильма изменено с " + oldFilm.getName() + " на " + newFilm.getName());
                oldFilm.setName(newFilm.getName());
            }
            //Описание
            if (newFilm.getDescription() != null && !newFilm.getDescription().isBlank()) {
                log.info("Описание фильма изменено с " + oldFilm.getDescription() + " на " + newFilm.getDescription());
                oldFilm.setDescription(newFilm.getDescription());
            }
            // Дата релиза
            if (newFilm.getReleaseDate() != null) {
                log.info("Дата релиза фильма изменена с " + oldFilm.getReleaseDate() + " на " + newFilm.getReleaseDate());
                oldFilm.setReleaseDate(newFilm.getReleaseDate());
            }
            //Продолжительность
            if (newFilm.getDuration() != null) {
                log.info("Длительность фильма изменена с " + oldFilm.getDuration() + " на " + newFilm.getDuration());
                oldFilm.setDuration(newFilm.getDuration());
            }
            return oldFilm;
        }
        throw new NotFoundException("Фильм с id = " + newFilm.getId() + " не найден");
    }

    @Override
    public Optional<Film> getFilmById(Long id) {
        return Optional.ofNullable(films.get(id));
    }

    @Override
    public Collection<Film> getAllFilms() {
        return films.values();
    }

    @Override
    public Collection<Film> getTopFilm(int count) {
        return getAllFilms().stream()
                .sorted(Comparator.comparingInt((Film film) -> film.getLikes().size()).reversed())
                .limit(count)
                .collect(Collectors.toList());
    }
}
