package ru.yandex.practicum.filmorate.dao.film;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.dao.BaseRepository;
import ru.yandex.practicum.filmorate.model.Likes;

@Repository
public class LikesDbStorage extends BaseRepository {
    private static final String ADD_LIKES = "INSERT INTO likes (user_id, film_id) VALUES (?, ?)";
    private static final String DELETE_LIKES = "DELETE FROM likes WHERE user_id = ? AND film_id = ?";

    public LikesDbStorage(JdbcTemplate jdbcTemplate, RowMapper<Likes> rowMapper) {
        super(jdbcTemplate, rowMapper);
    }

    public void addLike(Long userId, Long filmId) {
        int result = getJdbcTemplate().update(ADD_LIKES, userId, filmId);
        if (result <= 0) {
            new IllegalAccessException("Не удалось добавить лайк");
        }
    }

    public void removeLike(Long userId, Long filmId) {
        int result = getJdbcTemplate().update(DELETE_LIKES, userId, filmId);
        if (result <= 0) {
            new IllegalAccessException("Не удалось удалить");
        }
    }
}
