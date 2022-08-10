package ru.yandex.practicum.filmorate.dao.impl;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.Genre;

import java.util.Collection;
import java.util.stream.Collectors;

@Slf4j
@Component
public class DaoGenre {
    private final JdbcTemplate jdbcTemplate;

    @Autowired
    public DaoGenre(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public Collection<Genre> getAll() {
        String sql = "SELECT ID, NAME FROM GENRES ORDER BY ID";
        return jdbcTemplate.queryForStream(sql,
                        (rs, rowNum) -> new Genre(rs.getInt("id"), rs.getString("name")))
                .collect(Collectors.toUnmodifiableList());
    }

    public Genre getById(Long id) {
        String sql = "SELECT ID, NAME FROM GENRES WHERE id = ?";
        return jdbcTemplate.queryForObject(sql,
                (rs, rowNum) -> new Genre(rs.getInt("id"), rs.getString("name")),
                id);
    }
}
