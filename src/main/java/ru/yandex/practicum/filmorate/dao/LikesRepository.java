package ru.yandex.practicum.filmorate.dao;

import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.HashSet;
import java.util.Set;

@Repository
@RequiredArgsConstructor
public class LikesRepository {
    private final JdbcTemplate jdbcTemplate;

    public Set<Long> loadLikes(Long id) {
        String sql = "SELECT USER_ID FROM LIKES WHERE FILM_ID = ?";
        return new HashSet<>(jdbcTemplate.query(sql, (rs, rowNum) -> rs.getLong("USER_ID"), id));
    }

    public boolean deleteLike(Long filmId, Long userId) {
        String sql = "DELETE FROM LIKES WHERE FILM_ID = ? AND USER_ID = ?";
        return jdbcTemplate.update(sql, filmId, userId) > 0;
    }

    public boolean addLike(Long filmId, Long userId) {
        String sql = "MERGE INTO LIKES (FILM_ID, USER_ID) VALUES (?, ?)";
        return jdbcTemplate.update(sql, filmId, userId) > 0;
    }
}
