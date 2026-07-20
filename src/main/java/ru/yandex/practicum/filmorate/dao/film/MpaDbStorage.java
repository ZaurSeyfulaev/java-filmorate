package ru.yandex.practicum.filmorate.dao.film;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.dao.BaseRepository;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Mpa;

import java.util.List;
import java.util.Optional;

@Repository
public class MpaDbStorage extends BaseRepository {

    private static final String SELECT_MPA_BY_ID = "SELECT * FROM Mpa WHERE id = ?";
    private static final String SELECT_MPA = "SELECT * FROM Mpa";

    public MpaDbStorage(JdbcTemplate jdbcTemplate, RowMapper<Mpa> rowMapper) {
        super(jdbcTemplate, rowMapper);

    }

    public Optional<Mpa> selectMpaById(long id) {

        return findOne(SELECT_MPA_BY_ID, id);
    }

    public Long getMpaId(Film film) {

        Long mpaId = film.getMpa().getId();

        return mpaId;
    }

    public List<Mpa> selectMpa() {
        return findMany(SELECT_MPA);
    }
}
