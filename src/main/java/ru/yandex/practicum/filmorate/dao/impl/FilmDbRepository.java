package ru.yandex.practicum.filmorate.dao.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.dao.*;
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
    private final GenreRepository genreRepository;
    private final LikesRepository likesRepository;
    private final DirectorRepository directorRepository;

    private final DirectorRepository directorRepository;
    
    private final JdbcTemplate jdbcTemplate;

    @Override
    public Collection<Film> getAll() {
        String sql = "SELECT f.*, m.NAME MPA_NAME FROM FILMS f, MPA m WHERE f.MPA_ID = m.ID";
        Collection<Film> films = jdbcTemplate.query(sql, FilmDbRepository::makeFilm);
        films.forEach(x -> x.setLikes(likesRepository.loadLikes(x.getId())));
        genreRepository.loadFilmGenres(films);
        directorRepository.loadFilmDirectors(films);
        return films;
    }

    @Override
    public Collection<Film> getFilmByDirectorSorted(Integer id, String sort){
        String sql ="";
        if (sort.equalsIgnoreCase("year")){
        sql = "SELECT f.*,\n" +
                "        m.NAME MPA_NAME\n" +
                "        FROM FILMS f, MPA m\n" +
                "        WHERE f.MPA_ID = m.ID AND f.ID in (\n" +
                "            SELECT fd.FILM_ID\n" +
                "            FROM FILM_DIRECTOR as fd\n" +
                "            WHERE fd.DIRECTOR_ID = ?\n" +
                "            )  \n" +
                "ORDER BY f.RELEASE_DATE;";}
        else if(sort.equalsIgnoreCase("likes")){
        sql = "SELECT f.*\n" +
                "       m.NAME MPA_NAME\n" +
                "           FROM FILMS f\n" +
                "    join MPA m on m.ID = f.MPA_ID\n" +
                "WHERE (select f.ID from FILMS\n" +
                "    left join LIKES L on FILMS.ID = L.FILM_ID\n" +
                "    WHERE f.ID in (\n" +
                "    SELECT fd.FILM_ID\n" +
                "    FROM FILM_DIRECTOR as fd\n" +
                "    WHERE fd.DIRECTOR_ID = ?\n" +
                "    )\n" +
                "    group by f.ID\n" +
                "    order by count(l.USER_ID)desc)\n" +
                ";";
        }
        Collection<Film> films = jdbcTemplate.query(sql,FilmDbRepository::makeFilm,id);
        films.forEach(x -> x.setLikes(likesRepository.loadLikes(x.getId())));
        genreRepository.loadFilmGenres(films);
        directorRepository.loadFilmDirectors(films);
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
        if(film.getGenres() != null && film.getGenres().size() > 0){
            genreRepository.saveFilmGenres(List.of(film));
        }
        if(film.getDirectors() != null && film.getDirectors().size() > 0){
            directorRepository.saveFilmDirectors(List.of(film));
        }
        return film;
    }

    @Override
    public Film update(Film film) {
        String sql = "UPDATE FILMS SET NAME = ?, DESCRIPTION = ?, RELEASE_DATE = ?, DURATION = ?, MPA_ID = ?\n" +
                " WHERE id = ?";
        int countUpdateRows = jdbcTemplate.update(sql,
                film.getName(),
                film.getDescription(),
                Date.valueOf(film.getReleaseDate()),
                film.getDuration(),
                film.getMpa().getId(),
                film.getId());
        if(countUpdateRows > 0){
            genreRepository.saveFilmGenres(List.of(film));
            genreRepository.loadFilmGenres(List.of(film));
            directorRepository.saveFilmDirectors(List.of(film));
            directorRepository.loadFilmDirectors(List.of(film));
            film.setLikes(likesRepository.loadLikes(film.getId()));
            return film;
        }
        return null;
    }

    @Override
    public boolean delete(Long filmId) {
        String sql = "DELETE FROM FILMS WHERE ID = ?";
        return jdbcTemplate.update(sql, filmId) > 0;
    }

    @Override
    public Film getById(Long id) {
        String sql = "SELECT f.*, m.NAME MPA_NAME FROM FILMS f, MPA m WHERE f.ID = ? AND f.MPA_ID = m.ID";
        List<Film> films = jdbcTemplate.query(sql, FilmDbRepository::makeFilm, id);
        if (films.size() != 1){
            return null;
        }
        genreRepository.loadFilmGenres(films);
        directorRepository.loadFilmDirectors(films);
        loadLikes(films);
        return films.get(0);
    }

    @Override
    public void loadLikes(Collection<Film> films){
        films.forEach(x -> x.setLikes(likesRepository.loadLikes(x.getId())));
    }

    @Override
    public boolean addLike(Long filmId, Long userId) {
        return likesRepository.addLike(filmId, userId);
    }

    @Override
    public boolean deleteLike(Long filmId, Long userId) {
        return likesRepository.deleteLike(filmId, userId);
    }

    private static Film makeFilm(ResultSet rs, int i) throws SQLException {
        return new Film(rs.getLong("ID"),
                rs.getString("NAME"),
                rs.getString("DESCRIPTION"),
                rs.getDate("RELEASE_DATE").toLocalDate(),
                rs.getInt("DURATION"),
                new Mpa(rs.getInt("MPA_ID"), rs.getString("MPA_NAME")),
                new HashSet<>(),
                new ArrayList<>(),new ArrayList<>());
    }
}
