package ru.yandex.practicum.filmorate.dao.user;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.dao.BaseRepository;
import ru.yandex.practicum.filmorate.enums.FriendStatus;
import ru.yandex.practicum.filmorate.model.Friendship;
import ru.yandex.practicum.filmorate.model.User;

import java.util.List;

@Repository
public class FriendshipDbStorage extends BaseRepository {
    private static final String ADD_FRIENDS = "INSERT INTO Friends (user_id, friend_id, friend_status_id) VALUES (?, ?, ?)";
    private static final String SELECT_FRIENDSHIP = "SELECT * FROM Friends WHERE user_id = ? AND friend_id = ?";
    private static final String DELETE_FRIENDSHIP = "DELETE FROM Friends WHERE user_id = ? AND friend_id = ?";
    private static final String SELECT_ALL_USER_FRIENDSHIPS = "SELECT u.* " +
            "FROM \"User\" u " +
            "JOIN Friends f ON u.id = f.friend_id " +
            "JOIN Friend_Status fs ON f.friend_status_id = fs.status_id " +
            "WHERE f.user_id = ? " +
            "AND fs.status_type = TRUE;";

    private static final String SELECT_COMMON_FRIENDS = "SELECT u.* " +
            "FROM \"User\"  u " +
            "JOIN Friends f1 ON u.id = f1.friend_id " +
            "JOIN Friends f2 ON u.id = f2.friend_id " +
            "JOIN Friend_Status fs1 ON f1.friend_status_id = fs1.status_id " +
            "JOIN Friend_Status fs2 ON f2.friend_status_id = fs2.status_id " +
            "WHERE f1.user_id = ? " +
            "  AND f2.user_id = ? " +
            "  AND fs1.status_type = TRUE " +
            "  AND fs2.status_type = TRUE;";

    private final RowMapper<User> userRowMapper;

    public FriendshipDbStorage(JdbcTemplate jdbcTemplate,
                               @Qualifier("userRowMapper") RowMapper<User> userRowMapper,
                               @Qualifier("friendshipRowMapper") RowMapper<Friendship> rowMapper) {
        super(jdbcTemplate, rowMapper);
        this.userRowMapper = userRowMapper;
    }

    public void addFriendship(Long userId, Long friendId) {
        getJdbcTemplate().update(ADD_FRIENDS, userId, friendId,
                FriendStatus.CONFIRMED.getId());
    }

    public List<Friendship> getFriendship(Long userId, Long friendId) {
        return findMany(SELECT_FRIENDSHIP,
                userId,
                friendId);
    }

    public void deleteFriendship(Long userId, Long friendId) {
        delete(DELETE_FRIENDSHIP, userId, friendId);
    }

    public List<User> getAllUserFriends(Long userId) {

        return getJdbcTemplate().query(SELECT_ALL_USER_FRIENDSHIPS, userRowMapper, userId);
    }

    public List<User> getCommonUserFriends(Long userId1, Long userId2) {
        return getJdbcTemplate().query(SELECT_COMMON_FRIENDS, userRowMapper, userId1, userId2);
    }
}
