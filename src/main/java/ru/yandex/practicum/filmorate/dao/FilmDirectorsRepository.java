package ru.yandex.practicum.filmorate.dao;

import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.model.Film;

import java.util.Collection;

@Repository
@RequiredArgsConstructor
public class FilmDirectorsRepository {
    private final JdbcTemplate jdbcTemplate;

    public void deleteFilmDirectors(Collection<Film> films) {
        String sqlDelete = "DELETE FROM FILM_DIRECTOR WHERE FILM_ID = ?";
        films.forEach(x -> jdbcTemplate.update(sqlDelete, x.getId()));
    }
    public void deleteFilmDirectors(Long id) {
        String sqlDelete = "DELETE FROM FILM_DIRECTOR WHERE FILM_ID = ?";
        jdbcTemplate.update(sqlDelete, id);
    }

    public void deleteFilmDirectors(Integer id) {
        String sqlDelete = "DELETE FROM FILM_DIRECTOR WHERE DIRECTOR_ID = ?";
        jdbcTemplate.update(sqlDelete, id);
    }

    public void saveFilmDirectors(Collection<Film> films) {
        String sqlSave = "MERGE INTO FILM_DIRECTOR (FILM_ID, DIRECTOR_ID) VALUES (?, ?)";
        films.forEach(x -> {
            if(x.getDirectors() != null){
                x.getDirectors().forEach(director -> jdbcTemplate.update(sqlSave, x.getId(), director.getId()));
            }
        });
    }

}
