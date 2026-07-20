package ru.yandex.practicum.filmorate.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.dao.film.FilmGenreDbStorage;
import ru.yandex.practicum.filmorate.exceptions.NotFoundException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genres;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Slf4j
@Service
public class FilmGenreService {
    private final FilmGenreDbStorage filmGenreDbStorage;

    public FilmGenreService(FilmGenreDbStorage filmGenreDbStorage) {
        this.filmGenreDbStorage = filmGenreDbStorage;
    }

    public void addFilmGenre(Film film) {
        List<Genres> filmGenres = film.getGenres();
        Long filmId = film.getId();
        if (film == null) {
            throw new NotFoundException("Передан Null");
        }
        ;

        if (filmGenres == null || filmGenres.isEmpty()) {
            return;
        }

        List<Long> genreIds = filmGenres
                .stream()
                .map(Genres::getId)
                .map(id -> {
                            getGenreById(id).orElseThrow(() ->
                                    new NotFoundException("Genre с id " + filmId + " не найден"));
                            return id;
                        }
                ).distinct()
                .toList();
        filmGenreDbStorage.addFilmGenre(filmId, genreIds);
    }

    public List<Genres> getFilmGenre(Long filmId) {
        if (filmId == null) {
            throw new NotFoundException("Не передан Id");
        }

        Map<Long, List<Genres>> genresMap = filmGenreDbStorage.getFilmGenre();
        List<Genres> genres = genresMap.get(filmId);

        log.info("genres = " + genres);
        if (genres == null || genres.isEmpty()) {
            return new ArrayList<>();
        }
        return genres;
    }

    public Optional<Genres> getGenreById(Long id) {
        return filmGenreDbStorage.getGenreById(id);
    }
}
