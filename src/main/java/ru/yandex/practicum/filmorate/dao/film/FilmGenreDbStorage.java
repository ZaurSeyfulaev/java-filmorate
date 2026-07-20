package ru.yandex.practicum.filmorate.dao.film;


import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.dao.BaseRepository;
import ru.yandex.practicum.filmorate.model.Genres;

import java.util.*;


@Repository
public class FilmGenreDbStorage extends BaseRepository {
    private static final String ADD_FILM_GENRE = "INSERT INTO FILM_GENRE (film_id, genre_id) VALUES (?, ?)";
    private static final String SELECT_FILM_GENRES = "SELECT fg.film_id, gt.id, gt.name, FROM FILM_GENRE fg " +
            " JOIN GENRES gt ON fg.genre_id = gt.id";
    private static final String SELECT_GENRE_BY_ID = "SELECT * FROM GENRES WHERE id = ?";
    ;

    public FilmGenreDbStorage(JdbcTemplate jdbcTemplate, RowMapper<Genres> rowMapper) {
        super(jdbcTemplate, rowMapper);

    }

    public void addFilmGenre(Long filmId, List<Long> genreIds) {

        getJdbcTemplate().batchUpdate(ADD_FILM_GENRE, genreIds, genreIds.size(),
                (ps, genreId) -> {
                    ps.setLong(1, filmId);
                    ps.setLong(2, genreId);
                });
    }

    public Map<Long, List<Genres>> getFilmGenre() {
        Map<Long, List<Genres>> mapGenres = new HashMap<>();
        getJdbcTemplate().query(SELECT_FILM_GENRES, rs -> {
                    Long filmId = rs.getLong("film_id");
                    Genres genre = new Genres();
                    genre.setId(rs.getLong("id"));
                    genre.setName(rs.getString("name"));
                    mapGenres.putIfAbsent(filmId, new ArrayList<>());
                    mapGenres.get(filmId).add(genre);
                }
        );

        return mapGenres;
    }

    public Optional<Genres> getGenreById(Long id) {
        return findOne(SELECT_GENRE_BY_ID, id);
    }
}
