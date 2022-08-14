package ru.yandex.practicum.filmorate.dao.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.dao.UserRepository;
import ru.yandex.practicum.filmorate.model.User;

import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;

@Repository
@RequiredArgsConstructor
public class UserDbRepository implements UserRepository {
    private final JdbcTemplate jdbcTemplate;

    @Override
    public User add(User user) {
        String sql = "INSERT INTO USERS (LOGIN, NAME, EMAIL, BIRTHDAY) VALUES (?,?,?,?)";
        KeyHolder keyHolder = new GeneratedKeyHolder();
        jdbcTemplate.update(connection -> {
            PreparedStatement stmt = connection.prepareStatement(sql, new String[]{"id"});
            stmt.setString(1, user.getLogin());
            stmt.setString(2, user.getName());
            stmt.setString(3, user.getEmail());
            stmt.setDate(4, Date.valueOf(user.getBirthday()));
            return stmt;
        }, keyHolder);
        user.setId(keyHolder.getKey().longValue());
        return user;
    }

    @Override
    public User update(User user) {
        String sql = "UPDATE USERS SET LOGIN = ?, NAME = ?, EMAIL = ?, BIRTHDAY = ?\n" +
                " WHERE id = ?";
        int countUpdateRows = jdbcTemplate.update(sql,
                user.getLogin(),
                user.getName(),
                user.getEmail(),
                Date.valueOf(user.getBirthday()),
                user.getId());
        if(countUpdateRows > 0){
            return user;
        }
        return null;
    }

    @Override
    public boolean delete(Long id) {
        String sqlDelFromLikes = "DELETE FROM LIKES WHERE USER_ID = ?";
        jdbcTemplate.update(sqlDelFromLikes, id);
        String sqlDelFromFriends = "DELETE FROM FRIENDS WHERE USER_ID = ? OR FRIEND_ID = ?";
        jdbcTemplate.update(sqlDelFromFriends, id, id);
        String sqlDelFromUsers = "DELETE FROM USERS WHERE ID = ?";
        return jdbcTemplate.update(sqlDelFromUsers, id) > 0;
    }

    @Override
    public Collection<User> getAll() {
        String sql = "SELECT * FROM USERS ORDER BY ID";
        return jdbcTemplate.query(sql, UserDbRepository::makeUser);
    }

    @Override
    public User getById(Long id){
        Set<Long> friends = new HashSet<>();
        String sqlFriendsIds = "SELECT FRIEND_ID FROM FRIENDS WHERE USER_ID = ?";
        List<Long> friendsIds = jdbcTemplate.query(sqlFriendsIds, (rs, rowNum) -> rs.getLong("friend_id"), id);
        if (friendsIds.size() != 0){
            friends.addAll(friendsIds);
        }

        String sqlUser = "SELECT * FROM USERS WHERE ID = ?";
        List<User> userList = jdbcTemplate.query(sqlUser, UserDbRepository::makeUser, id);
        if(userList.size() != 1){
            return null;
        }
        User user = userList.get(0);
        user.setFriendsId(friends);
        return user;
    }

    public Set<User> getFriends(Long id) {
        String sqlFriends = "SELECT u.*\n" +
                "FROM USERS u, FRIENDS f\n" +
                "WHERE f.USER_ID = ? AND u.ID = f.FRIEND_ID";
        List<User> friends = jdbcTemplate.query(sqlFriends, UserDbRepository::makeUser, id);
        return new HashSet<>(friends);
    }

    @Override
    public void addFriends(Long userId, Long friendId) {
        String sql = "INSERT INTO FRIENDS (USER_ID, FRIEND_ID) VALUES (?,?)";
        jdbcTemplate.update(sql, userId, friendId);
    }

    @Override
    public void deleteFriend(Long userId, Long friendId) {
        String sql = "DELETE FROM FRIENDS WHERE USER_ID = ? AND FRIEND_ID = ?";
        jdbcTemplate.update(sql, userId, friendId);
    }

    @Override
    public Set<User> searchCommonFriends(Long userId, Long otherId) {
        String sql = "SELECT u.*\n" +
                "FROM USERS u, FRIENDS f1, FRIENDS f2\n" +
                "WHERE u.ID = f1.FRIEND_ID\n" +
                "AND u.ID = f2.FRIEND_ID\n" +
                "AND f1.USER_ID = ?\n" +
                "AND f2.USER_ID = ?";
        List<User> users = jdbcTemplate.query(sql, UserDbRepository::makeUser, userId, otherId);
        return new HashSet<>(users);
    }

    private static User makeUser(ResultSet rs, int rowNum) throws SQLException {
        return new User(rs.getLong("id"),
                rs.getString("email"),
                rs.getString("login"),
                rs.getString("name"),
                rs.getDate("birthday").toLocalDate(),
                new HashSet<>());
    }
}
