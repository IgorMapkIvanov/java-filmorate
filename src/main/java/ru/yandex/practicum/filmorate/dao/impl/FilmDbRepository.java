package ru.yandex.practicum.filmorate.dao.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.dao.FilmRepository;
import ru.yandex.practicum.filmorate.dao.GenreRepository;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Mpa;

import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;


@Repository
@RequiredArgsConstructor
public class FilmDbRepository implements FilmRepository {

    private final JdbcTemplate jdbcTemplate;
    private final GenreRepository genreRepository;

    @Override
    public Collection<Film> getAll() {
        String sql = "SELECT f.*, m.NAME MPA_NAME FROM FILMS f, MPA m ORDER BY ID";
        Collection<Film> films = jdbcTemplate.query(sql, FilmDbRepository::makeFilm);
        films.forEach(x -> x.setLikes(loadLikes(x.getId())));
        genreRepository.loadFilmGenres(films);
        return films;
    }

    @Override
    public Film add(Film film) {
        String sql = "INSERT INTO FILMS (NAME, DESCRIPTION,DURATION, RELEASE_DATE, MPA_ID) VALUES (?,?,?,?,?)";
        KeyHolder keyHolder = new GeneratedKeyHolder();
        jdbcTemplate.update(connection -> {
            PreparedStatement stmt = connection.prepareStatement(sql, new String[]{"id"});
            stmt.setString(1, film.getName());
            stmt.setString(2, film.getDescription());
            stmt.setInt(3, film.getDuration());
            stmt.setDate(4, Date.valueOf(film.getReleaseDate()));
            stmt.setInt(5, film.getMpa().getId());
            return stmt;
        }, keyHolder);
        film.setId(keyHolder.getKey().longValue());
        return film;
    }

    @Override
    public Film update(Film film) {
        String sql = "UPDATE FILMS SET NAME = ?, DESCRIPTION = ?, RELEASE_DATE = ?, DURATION = ?, MPA_ID = ?\n" +
                " WHERE id = ?";
        jdbcTemplate.update(sql,
                film.getName(),
                film.getDescription(),
                Date.valueOf(film.getReleaseDate()),
                film.getDuration(),
                film.getMpa().getId());
        genreRepository.loadFilmGenres(List.of(film));
        film.setLikes(loadLikes(film.getId()));
        return film;
    }

    @Override
    public boolean delete(Long filmId) {
        String sql = "DELETE FROM FILMS WHERE ID = ?";
        return jdbcTemplate.update(sql, filmId) > 0;
    }

    @Override
    public Film getById(Long id) {
        String sql = "SELECT f.*, m.NAME MPA_NAME FROM FILMS f, MPA m WHERE f.ID = ?";
        List<Film> films = jdbcTemplate.query(sql, FilmDbRepository::makeFilm, id);
        if (films.size() != 1){
            return null;
        }
        return films.get(0);
    }

    @Override
    public void loadLikes(Collection<Film> films){
        films.forEach(x -> x.setLikes(loadLikes(x.getId())));
    }

    @Override
    public void addLike(Long filmId, Long userId) {
        String sql = "MERGE INTO LIKES (FILM_ID, USER_ID) VALUES (?, ?)";
        jdbcTemplate.update(sql, filmId, userId);
    }

    @Override
    public void deleteLike(Long filmId, Long userId) {
        String sql = "DELETE FROM LIKES WHERE FILM_ID = ? AND USER_ID = ?";
        jdbcTemplate.update(sql, filmId, userId);
    }

    private Set<Long> loadLikes(Long id) {
        String sql = "SELECT USER_ID FROM LIKES WHERE FILM_ID = ?";
        return new HashSet<>(jdbcTemplate.query(sql, (rs, rowNum) -> rs.getLong("USER_ID"), id));
    }

    private static Film makeFilm(ResultSet rs, int i) throws SQLException {
        return new Film(rs.getLong("ID"),
                rs.getString("NAME"),
                rs.getString("DESCRIPTION"),
                rs.getDate("RELEASE_DATE").toLocalDate(),
                rs.getInt("DURATION"),
                new Mpa(rs.getInt("MPA_ID"), rs.getString("MPA_NAME")),
                new HashSet<>(),
                new ArrayList<>());
    }
}
