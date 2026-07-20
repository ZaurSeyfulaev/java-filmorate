package ru.yandex.practicum.filmorate.dao.film;


import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.dao.BaseRepository;
import ru.yandex.practicum.filmorate.model.Film;

import java.util.List;
import java.util.Optional;

@Repository
public class FilmDbStorage extends BaseRepository<Film> {
    private static final String SELECT_ALL_FILM = "SELECT f.*," +
            "    m.id AS mpa_id," +
            "    m.name AS mpa_name" +
            " FROM film f" +
            " LEFT JOIN Mpa m ON f.mpa_id = m.id";
    private static final String SELECT_FILM_BY_ID = "SELECT f.*," +
            "  m.id AS mpa_id," +
            "  m.name AS mpa_name " +
            "FROM film f " +
            "LEFT JOIN Mpa m ON f.mpa_id = m.id " +
            "WHERE f.id = ?";
    private static final String SELECT_TOP_FILM = "SELECT f.*, " +
            "  COUNT(l.user_id) AS likes_count, " +
            "  mpa.id AS mpa_id, " +
            "  mpa.name AS mpa_name " +
            " FROM Film f" +
            "   LEFT JOIN Likes l ON f.id = l.film_id" +
            "   LEFT JOIN Mpa mpa ON mpa.id = f.MPA_ID" +
            " GROUP BY f.id, f.name" +
            " ORDER BY likes_count DESC" +
            " LIMIT ?;";
    private static final String ADD_FILM = "INSERT INTO Film (name, description, duration, release_date, mpa_id)"
            + "VALUES (?, ?, ?, ?, ?)";
    private static final String UPDATE_FILM = "UPDATE film SET name = ?, description = ?, duration = ?, release_date = ?, mpa_id =  ? " +
            " WHERE id = ?";

    private static final String SELECT_FILM_GENRE = "SELECT" +
            "    fg.film_id, " +
            "    g.id, " +
            "    g.name" +
            " FROM film_genre fg" +
            " JOIN genre g ON fg.genre_id = g.id" +
            " ORDER BY fg.film_id";


    public FilmDbStorage(JdbcTemplate jdbcTemplate, @Qualifier("filmRowMapper") RowMapper<Film> rowMapper) {

        super(jdbcTemplate, rowMapper);

    }

    public List<Film> getAllFilms() {

        List<Film> films = findMany(SELECT_ALL_FILM);

        return films;
    }

    public List<Film> getTopFilms(int count) {

        List<Film> films = findMany(SELECT_TOP_FILM,
                count);

        return films;
    }

    public Film createFilm(Film film) {

        long id = insert(ADD_FILM,
                film.getName(),
                film.getDescription(),
                film.getDuration(),
                film.getReleaseDate(),
                film.getMpa().getId());
        film.setId(id);

        return film;
    }

    public Film updateFilm(Film film) {

        update(UPDATE_FILM,
                film.getName(),
                film.getDescription(),
                film.getDuration(),
                film.getReleaseDate(),
                film.getMpa().getId(),
                film.getId()
        );

        return film;
    }

    public Optional<Film> getFilmById(long id) {

        Optional<Film> optionalFilm = findOne(SELECT_FILM_BY_ID, id);

        return optionalFilm;
    }
}