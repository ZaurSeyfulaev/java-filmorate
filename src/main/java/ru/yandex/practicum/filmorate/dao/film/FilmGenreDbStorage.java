package ru.yandex.practicum.filmorate.dao.film;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.dao.BaseRepository;
import ru.yandex.practicum.filmorate.exceptions.NotFoundException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genres;

import java.util.List;
import java.util.Optional;

@Repository
public class FilmGenreDbStorage extends BaseRepository {
    private static final String ADD_FILM_GENRE = "INSERT INTO FILM_GENRE (film_id, genre_id) VALUES (?, ?)";
    private static final String SELECT_FILM_GENRES = "SELECT gt.* FROM FILM_GENRE fg " +
            " JOIN GENRES gt ON fg.genre_id = gt.id WHERE fg.film_id = ?";
    private static final String SELECT_GENRE_BY_ID = "SELECT * FROM GENRES WHERE id = ?";

    public FilmGenreDbStorage(JdbcTemplate jdbcTemplate, RowMapper<Genres> rowMapper) {
        super(jdbcTemplate, rowMapper);
    }

    public void addFilmGenre(Film film) {
        List<Genres> filmGenres = film.getGenres();
        Long filmId = film.getId();
        if (filmGenres == null || filmGenres.isEmpty()) {
            return;
        }
        List<Long> genreIds = filmGenres
                .stream()
                .map(Genres::getId)
                .map(id ->
                        {
                            getGenreById(id).orElseThrow(() ->
                                    new NotFoundException("Genre с id " + filmId + " не найден"));
                            return id;
                        }
                ).distinct()
                .toList();

        getJdbcTemplate().batchUpdate(ADD_FILM_GENRE, genreIds, genreIds.size(),
                (ps, genreId) -> {
                    ps.setLong(1, filmId);
                    ps.setLong(2, genreId);
                });
    }

    public List<Genres> getFilmGenre(Long filmId) {
        return findMany(SELECT_FILM_GENRES, filmId);
    }

    public Optional<Genres> getGenreById(Long id) {
        return findOne(SELECT_GENRE_BY_ID, id);
    }
}
