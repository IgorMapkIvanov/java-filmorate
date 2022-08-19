package ru.yandex.practicum.filmorate.dao.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.dao.EventDbRepository;
import ru.yandex.practicum.filmorate.dao.FriendsRepository;
import ru.yandex.practicum.filmorate.dao.UserRepository;
import ru.yandex.practicum.filmorate.model.EventOperation;
import ru.yandex.practicum.filmorate.model.EventType;
import ru.yandex.practicum.filmorate.model.User;

import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;

@Repository
@RequiredArgsConstructor
public class UserDbRepository implements UserRepository {
    private final FriendsRepository friendsRepository;
    private final EventDbRepository eventDbRepository;

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
        List<Long> friendsIds = friendsRepository.getFriendsIds(id);
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
        return new HashSet<>(friendsRepository.getFriends(id));
    }

    @Override
    public void addFriends(Long userId, Long friendId) {
        friendsRepository.addFriends(userId, friendId);
        eventDbRepository.addEvent(userId, friendId, EventType.FRIEND, EventOperation.ADD);
    }

    @Override
    public void deleteFriend(Long userId, Long friendId) {
        friendsRepository.deleteFriend(userId,friendId);
        eventDbRepository.addEvent(userId, friendId, EventType.FRIEND, EventOperation.DELETE);
    }

    @Override
    public Set<User> searchCommonFriends(Long userId, Long otherId) {
        return new HashSet<>(friendsRepository.commonFriends(userId, otherId));
    }

    public static User makeUser(ResultSet rs, int rowNum) throws SQLException {
        return new User(rs.getLong("id"),
                rs.getString("email"),
                rs.getString("login"),
                rs.getString("name"),
                rs.getDate("birthday").toLocalDate(),
                new HashSet<>());
    }
}
