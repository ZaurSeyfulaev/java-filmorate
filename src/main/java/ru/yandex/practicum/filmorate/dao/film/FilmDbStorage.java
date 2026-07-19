package ru.yandex.practicum.filmorate.dao.film;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.dao.BaseRepository;
import ru.yandex.practicum.filmorate.exceptions.NotFoundException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genres;
import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.storage.film.FilmStorage;

import java.util.List;
import java.util.Optional;

@Repository
public class FilmDbStorage extends BaseRepository<Film> {
    private static final String SELECT_ALL_FILM = "SELECT * from film";
    private static final String SELECT_FILM_BY_ID = "SELECT * FROM film WHERE id = ?";
    private static final String SELECT_TOP_FILM = "SELECT f.*, COUNT(l.user_id) AS likes_count" +
            " FROM Film f" +
            " LEFT JOIN Likes l ON f.id = l.film_id" +
            " GROUP BY f.id, f.name" +
            " ORDER BY likes_count DESC" +
            " LIMIT ?";
    private static final String ADD_FILM = "INSERT INTO Film (name, description, duration, release_date, mpa_id)"
            + "VALUES (?, ?, ?, ?, ?)";
    private static final String UPDATE_FILM = "UPDATE film SET name = ?, description = ?, duration = ?, release_date = ?, mpa_id = ? " +
            " WHERE id = ?";

    FilmGenreDbStorage filmGenreDbStorage;
    MpaDbStorage mpaDbStorage;

    public FilmDbStorage(JdbcTemplate jdbcTemplate, @Qualifier("filmRowMapper") RowMapper<Film> rowMapper,
                         FilmGenreDbStorage filmGenreDbStorage, FilmStorage filmStorage, MpaDbStorage mpaDbStorage) {
        super(jdbcTemplate, rowMapper);
        this.filmGenreDbStorage = filmGenreDbStorage;
        this.mpaDbStorage = mpaDbStorage;
    }

    public List<Film> getAllFilms() {
        List<Film> films = findMany(SELECT_ALL_FILM);

        for (Film film : films) {
            filmGenreDbStorage.getFilmGenre(film.getId());
            mpaDbStorage.selectMpaByFilmId(film.getId());
        }
        return films;
    }

    public List<Film> getTopFilms(int count) {
        List<Film> films = findMany(SELECT_TOP_FILM,
                count);

        for (Film film : films) {
            filmGenreDbStorage.getFilmGenre(film.getId());
            mpaDbStorage.selectMpaByFilmId(film.getId());
        }
        return films;
    }

    public Film createFilm(Film film) {

        Long mpaId = mpaDbStorage.getMpaId(film);
        long id = insert(ADD_FILM,
                film.getName(),
                film.getDescription(),
                film.getDuration(),
                film.getReleaseDate(),
                mpaId);
        film.setId(id);
        filmGenreDbStorage.addFilmGenre(film);

        return film;
    }

    public Film updateFilm(Film film) {
        Long mpaId = mpaDbStorage.getMpaId(film);

        update(UPDATE_FILM,
                film.getName(),
                film.getDescription(),
                film.getDuration(),
                film.getReleaseDate(),
                mpaId,
                film.getId()
        );
        if (film.getGenres() != null) {
            filmGenreDbStorage.addFilmGenre(film);
        }

        return film;
    }

    public Optional<Film> getFilmById(long id) {
        Optional<Film> optionalFilm = findOne(SELECT_FILM_BY_ID, id);
        Film fIlm = optionalFilm.orElseThrow(() -> new NotFoundException("Фильм не найден"));
        List<Genres> genres = filmGenreDbStorage.getFilmGenre(fIlm.getId());
        Mpa mpa = mpaDbStorage.selectMpaByFilmId(fIlm.getId()).get();
        fIlm.setGenres(genres);
        fIlm.setMpa(mpa);
        return Optional.of(fIlm);
    }
}