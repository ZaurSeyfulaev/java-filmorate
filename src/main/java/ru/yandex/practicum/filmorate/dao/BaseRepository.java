package ru.yandex.practicum.filmorate.dao;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import ru.yandex.practicum.filmorate.exceptions.InternalServerException;

import java.sql.PreparedStatement;
import java.sql.Statement;
import java.util.List;
import java.util.Optional;

@RequiredArgsConstructor
public class BaseRepository<T> {
    @Getter
    private final JdbcTemplate jdbcTemplate;
    private final RowMapper<T> rowMapper;

    public Optional<T> findOne(String query, Object... args) {
        try {
            T result = jdbcTemplate.queryForObject(query, args, rowMapper);
            return Optional.ofNullable(result);
        } catch (EmptyResultDataAccessException e) {
            return Optional.empty();
        }
    }

    public List<T> findMany(String query, Object... args) {
        return jdbcTemplate.query(query, args, rowMapper);
    }

    public void delete(String query, Object... args) {

        jdbcTemplate.update(query, args);
    }

    public void update(String query, Object... args) {
        jdbcTemplate.update(query, args);
    }

    public Long insert(String query, Object... args) {
        GeneratedKeyHolder keyHolder = new GeneratedKeyHolder();
        jdbcTemplate.update(connection -> {
            PreparedStatement ps = connection.prepareStatement(query, Statement.RETURN_GENERATED_KEYS);
            for (int i = 0; i < args.length; i++) {
                ps.setObject(i + 1, args[i]);
            }
            return ps;
        }, keyHolder);
        Long id = keyHolder.getKey().longValue();
        if (id != null) {
            return id;
        } else {
            throw new InternalServerException("Не удалось сохранить данные");
        }
    }
}

