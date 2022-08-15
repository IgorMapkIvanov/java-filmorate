package ru.yandex.practicum.filmorate.dao;

import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.dao.impl.UserDbRepository;
import ru.yandex.practicum.filmorate.model.User;

import java.util.List;

@Repository
@RequiredArgsConstructor
public class FriendsRepository {
    private final JdbcTemplate jdbcTemplate;
    public void deleteFriends(Long id) {
        String sqlDelFromFriends = "DELETE FROM FRIENDS WHERE USER_ID = ? OR FRIEND_ID = ?";
        jdbcTemplate.update(sqlDelFromFriends, id, id);
    }

    public List<Long> getFriendsIds(Long id) {
        String sqlFriendsIds = "SELECT FRIEND_ID FROM FRIENDS WHERE USER_ID = ?";
        return jdbcTemplate.query(sqlFriendsIds, (rs, rowNum) -> rs.getLong("friend_id"), id);
    }

    public List<User> getFriends(Long id) {
        String sqlFriends = "SELECT u.*\n" +
                "FROM USERS u, FRIENDS f\n" +
                "WHERE f.USER_ID = ? AND u.ID = f.FRIEND_ID";
        return jdbcTemplate.query(sqlFriends, UserDbRepository::makeUser, id);
    }

    public void addFriends(Long userId, Long friendId) {
        String sql = "INSERT INTO FRIENDS (USER_ID, FRIEND_ID) VALUES (?,?)";
        jdbcTemplate.update(sql, userId, friendId);
    }

    public void deleteFriend(Long userId, Long friendId) {
        String sql = "DELETE FROM FRIENDS WHERE USER_ID = ? AND FRIEND_ID = ?";
        jdbcTemplate.update(sql, userId, friendId);
    }

    public List<User> commonFriends(Long userId, Long otherId) {
        String sql = "SELECT u.*\n" +
                "FROM USERS u, FRIENDS f1, FRIENDS f2\n" +
                "WHERE u.ID = f1.FRIEND_ID\n" +
                "AND u.ID = f2.FRIEND_ID\n" +
                "AND f1.USER_ID = ?\n" +
                "AND f2.USER_ID = ?";
        return jdbcTemplate.query(sql, UserDbRepository::makeUser, userId, otherId);
    }
}
