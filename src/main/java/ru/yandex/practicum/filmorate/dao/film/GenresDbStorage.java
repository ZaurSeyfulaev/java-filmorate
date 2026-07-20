package ru.yandex.practicum.filmorate.dao.film;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.dao.BaseRepository;
import ru.yandex.practicum.filmorate.model.Genres;

import java.util.List;
import java.util.Optional;

@Repository
public class GenresDbStorage extends BaseRepository {
    private static final String SELECT_GENRE_TYPE_BY_ID = "SELECT * FROM Genres WHERE ID = ?";
    private static final String SELECT_GENRE_TYPE = "SELECT * FROM Genres";

    public GenresDbStorage(JdbcTemplate jdbcTemplate, RowMapper<ru.yandex.practicum.filmorate.model.Genres> rowMapper) {
        super(jdbcTemplate, rowMapper);
    }

    public Optional<Genres> selectGenreTypeById(Long id) {
        return findOne(SELECT_GENRE_TYPE_BY_ID, id);
    }

    public List<Genres> selectAllGenreTypes() {
        return findMany(SELECT_GENRE_TYPE);
    }
}
