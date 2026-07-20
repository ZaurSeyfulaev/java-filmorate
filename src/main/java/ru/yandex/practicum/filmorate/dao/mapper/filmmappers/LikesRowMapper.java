package ru.yandex.practicum.filmorate.dao.mapper.filmmappers;

import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.Likes;

import java.sql.ResultSet;
import java.sql.SQLException;

@Component
public class LikesRowMapper implements RowMapper<Likes> {
    @Override
    public Likes mapRow(ResultSet rs, int rowNum) throws SQLException {
        Likes likes = new Likes();
        likes.setUserId(rs.getLong("user_id"));
        likes.setMovieId(rs.getLong("film_id"));
        return likes;
    }
}
