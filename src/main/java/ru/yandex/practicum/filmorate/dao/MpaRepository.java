package ru.yandex.practicum.filmorate.dao;

import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.model.Mpa;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

@Repository
@RequiredArgsConstructor
public class MpaRepository {
    private final JdbcTemplate jdbcTemplate;

    public Collection<Mpa> getAll() {
        String sql = "SELECT ID, NAME FROM MPA ORDER BY ID";
        return jdbcTemplate.query(sql,
                MpaRepository::makeMpa)
                .stream()
                .collect(Collectors.toUnmodifiableList());
    }

    public Mpa getById(Long id) {
        String sql = "SELECT ID, NAME FROM MPA WHERE id = ?";
        List<Mpa> mpa = jdbcTemplate.query(sql, MpaRepository::makeMpa, id);
        if(mpa.size() != 1){
            return null;
        }
        return mpa.get(0);
    }

    private static Mpa makeMpa(ResultSet rs, int rowNum) throws SQLException {
        return new Mpa(rs.getInt("id"), rs.getString("name"));
    }
}
