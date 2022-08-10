package ru.yandex.practicum.filmorate.dao.impl;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.MPA;

import java.util.Collection;
import java.util.stream.Collectors;

@Slf4j
@Component
public class DaoMPA {
    private final JdbcTemplate jdbcTemplate;

    @Autowired
    public DaoMPA(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public Collection<MPA> getAll() {
        String sql = "SELECT ID, NAME FROM MPA ORDER BY ID";
        return jdbcTemplate.queryForStream(sql,
                        (rs, rowNum) -> new MPA(rs.getInt("id"), rs.getString("name")))
                .collect(Collectors.toUnmodifiableList());
    }

    public MPA getById(Long id) {
        String sql = "SELECT ID, NAME FROM MPA WHERE id = ?";
        return jdbcTemplate.queryForObject(sql,
                (rs, rowNum) -> new MPA(rs.getInt("id"), rs.getString("name")),
                id);
    }
}
