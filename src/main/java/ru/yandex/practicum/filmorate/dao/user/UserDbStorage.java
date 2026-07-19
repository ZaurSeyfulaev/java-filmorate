package ru.yandex.practicum.filmorate.dao.user;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.dao.BaseRepository;
import ru.yandex.practicum.filmorate.model.User;

import java.util.List;
import java.util.Optional;

@Repository
public class UserDbStorage extends BaseRepository {
    private static final String SELECT_USER_BY_ID = "SELECT * FROM \"User\" WHERE id = ?";
    private static final String SELECT_ALL_USERS = "SELECT * FROM \"User\"";
    private static final String CREATE_USER = "INSERT INTO \"User\" (username, email, login, birthday) VALUES (?,?,?,?)";
    private static final String UPDATE_USER = "UPDATE \"User\" SET username = ?, email = ?, login = ?, birthday = ? WHERE id = ?";

    public UserDbStorage(JdbcTemplate jdbcTemplate, @Qualifier("userRowMapper") RowMapper rowMapper) {
        super(jdbcTemplate, rowMapper);
    }

    public List<User> getAllUsers() {
        return findMany(SELECT_ALL_USERS);
    }

    public User createUser(User user) {
        long id = insert(CREATE_USER,
                user.getName(),
                user.getEmail(),
                user.getLogin(),
                user.getBirthday());
        user.setId(id);
        return user;
    }

    public User updateUser(User user) {
        update(UPDATE_USER,
                user.getName(),
                user.getEmail(),
                user.getLogin(),
                user.getBirthday(),
                user.getId());
        return user;
    }

    public Optional<User> getUserById(long id) {
        return findOne(SELECT_USER_BY_ID, id);
    }
}
