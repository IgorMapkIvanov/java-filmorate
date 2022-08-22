package ru.yandex.practicum.filmorate.dao;


import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.model.Director;
import ru.yandex.practicum.filmorate.model.Film;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Collection;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Repository
@RequiredArgsConstructor
public class DirectorRepository {


    private final FilmDirectorsRepository filmDirectorsRepository;
    private final JdbcTemplate jdbcTemplate;

    public Collection<Director> getAll() {
        String sql = "SELECT * FROM DIRECTORS ORDER BY ID";
        return jdbcTemplate.query(sql, DirectorRepository::makeDirector)
                .stream()
                .collect(Collectors.toUnmodifiableList());
    }

    public Director getById(Integer id) {
        String sql = "SELECT * FROM DIRECTORS WHERE id = ?";
        List<Director> directors = jdbcTemplate.query(sql, DirectorRepository::makeDirector, id);
        if(directors.size() != 1){
            return null;
        }
        return directors.get(0);
    }

    public Director add(Director director){
        String sql = "INSERT INTO DIRECTORS (NAME) VALUES (?)";
        KeyHolder keyHolder = new GeneratedKeyHolder();
        jdbcTemplate.update(connection -> {
            PreparedStatement stmt = connection.prepareStatement(sql, new String[]{"id"});
            stmt.setString(1, director.getName());
            return stmt;
        }, keyHolder);
        director.setId(Objects.requireNonNull(keyHolder.getKey()).intValue());
        return director;
    }

    public Director update(Director director){
        String sql = "UPDATE DIRECTORS SET DIRECTORS.NAME = ?" +
                " WHERE id = ?";
        int countUpdateRows = jdbcTemplate.update(sql,
                director.getName(),
                director.getId());
        if(countUpdateRows > 0){
            return director;
        }
        return null;
    }

    public boolean delete(Integer id) {
        String sql = "DELETE FROM DIRECTORS WHERE ID = ?";
        return jdbcTemplate.update(sql, id) > 0;
    }


    public void loadFilmDirectors(Collection<Film> films) {
        films.forEach(x -> x.setDirectors(loadDirector(x.getId())));
    }

    private Collection<Director> loadDirector(Long filmId) {
        String sql = "SELECT ds.ID, ds.NAME " +
                "FROM DIRECTORS ds, FILM_DIRECTOR fd " +
                "WHERE fd.FILM_ID = ? AND fd.DIRECTOR_ID = ds.ID";
        return jdbcTemplate.query(sql, DirectorRepository::makeDirector, filmId).stream()
                .sorted(Comparator.comparing(Director::getId, Integer::compareTo))
                .collect(Collectors.toUnmodifiableList());
    }

    public void saveFilmDirectors(Collection<Film> films) {
        filmDirectorsRepository.deleteFilmDirectors(films);
        filmDirectorsRepository.saveFilmDirectors(films);
    }

    private static Director makeDirector(ResultSet rs, int rowNum) throws SQLException {
        return new Director(rs.getInt("id"), rs.getString("name"));
    }

}
