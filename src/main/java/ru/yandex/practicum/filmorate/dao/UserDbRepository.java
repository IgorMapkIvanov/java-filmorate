package ru.yandex.practicum.filmorate.dao;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.model.User;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
public class UserDbRepository implements UserRepository{
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
            stmt.setString(4, user.getBirthday()
                    .format(DateTimeFormatter.ofPattern("yyyy-MM-dd")));
            return stmt;
        }, keyHolder);
        user.setId(keyHolder.getKey().longValue());
        return user;
    }

    @Override
    public User update(User user) {
        String sql = "UPDATE USERS SET " +
                "LOGIN = ?, NAME = ?, EMAIL = ?, BIRTHDAY = ? " +
                " WHERE id = ?";
        jdbcTemplate.update(sql,
                user.getLogin(),
                user.getName(),
                user.getEmail(),
                user.getBirthday().format(DateTimeFormatter.ofPattern("yyyy-MM-dd")),
                user.getId());
        return user;
    }

    @Override
    public boolean delete(Long id) {
        String sql = "DELETE FROM USERS WHERE ID = ?";
        return jdbcTemplate.update(sql, id) > 0;
    }

    @Override
    public Collection<User> getAll() {
        String sql = "SELECT * FROM USERS ORDER BY ID";
        return jdbcTemplate.queryForStream(sql,
                        (rs, rowNum) -> new User(rs.getLong("id"),
                                rs.getString("email"),
                                rs.getString("login"),
                                rs.getString("name"),
                                LocalDate.parse(rs.getString("birthday"),
                                        DateTimeFormatter.ofPattern("yyyy-MM-dd")),
                                new HashSet<>()))
                .collect(Collectors.toUnmodifiableList());
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
        String sqlFriends = "SELECT *\n" +
                "FROM PUBLIC.USERS\n" +
                "WHERE ID IN (SELECT USER_ID FROM FRIENDS WHERE FRIEND_ID = ?)";
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
                LocalDate.parse(rs.getString("birthday"),
                        DateTimeFormatter.ofPattern("yyyy-MM-dd")),
                new HashSet<>());
    }
}
