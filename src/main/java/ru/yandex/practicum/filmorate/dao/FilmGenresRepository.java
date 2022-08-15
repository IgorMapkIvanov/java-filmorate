package ru.yandex.practicum.filmorate.dao;

import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.model.Film;

import java.util.Collection;

@Repository
@RequiredArgsConstructor
public class FilmGenresRepository {

    private final JdbcTemplate jdbcTemplate;

    public void deleteFilmGenres(Collection<Film> films) {
        String sqlDelete = "DELETE FROM FILM_GENRES WHERE FILM_ID = ?";
        films.forEach(x -> jdbcTemplate.update(sqlDelete, x.getId()));
    }
    public void deleteFilmGenres(Long id) {
        String sqlDelete = "DELETE FROM FILM_GENRES WHERE FILM_ID = ?";
        jdbcTemplate.update(sqlDelete, id);
    }

    public void saveFilmGenres(Collection<Film> films) {
        String sqlSave = "MERGE INTO FILM_GENRES (FILM_ID, GENRE_ID) VALUES (?, ?)";
        films.forEach(x -> {
            if(x.getGenres() != null){
                x.getGenres().forEach(genre -> jdbcTemplate.update(sqlSave, x.getId(), genre.getId()));
            }
        });
    }
}
