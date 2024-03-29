package ru.yandex.practicum.filmorate.dao;

import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Collection;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Repository
@RequiredArgsConstructor
public class GenreRepository {
    private final FilmGenresRepository filmGenresRepository;
    private final JdbcTemplate jdbcTemplate;

    public Collection<Genre> getAll() {
        String sql = "SELECT * FROM GENRES ORDER BY ID";
        return jdbcTemplate.query(sql, GenreRepository::makeGenre)
                .stream()
                .collect(Collectors.toUnmodifiableList());
    }

    public Genre getById(Long id) {
        String sql = "SELECT * FROM GENRES WHERE id = ?";
        List<Genre> genres = jdbcTemplate.query(sql, GenreRepository::makeGenre, id);
        if(genres.size() != 1){
            return null;
        }
        return genres.get(0);
    }

    public void loadFilmGenres(Collection<Film> films) {
        films.forEach(x -> x.setGenres(loadGenres(x.getId())));
    }

    private Collection<Genre> loadGenres(Long filmId) {
        String sql = "SELECT g.ID, g.NAME FROM GENRES g, FILM_GENRES fg WHERE fg.FILM_ID = ? AND fg.GENRE_ID = g.ID";
        return jdbcTemplate.query(sql, GenreRepository::makeGenre, filmId).stream()
                .sorted(Comparator.comparing(Genre::getId, Integer::compareTo))
                .collect(Collectors.toUnmodifiableList());
    }

    public void saveFilmGenres(Collection<Film> films) {
        filmGenresRepository.deleteFilmGenres(films);
        filmGenresRepository.saveFilmGenres(films);
    }

    private static Genre makeGenre(ResultSet rs, int rowNum) throws SQLException {
        return new Genre(rs.getInt("id"), rs.getString("name"));
    }
}