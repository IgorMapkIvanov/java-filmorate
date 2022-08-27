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
import java.util.stream.Collectors;


@Repository
@RequiredArgsConstructor
public class FilmDbRepository implements FilmRepository {
    private final GenreRepository genreRepository;
    private final LikesRepository likesRepository;
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
    public Collection<Film> getFilmByDirectorSortedByYear(Long id) {
        String sql = "SELECT f.*,\n" +
                "        m.NAME MPA_NAME\n" +
                "        FROM FILMS f, MPA m \n" +
                "        WHERE f.MPA_ID = m.ID AND f.ID in (\n" +
                "            SELECT fd.FILM_ID\n" +
                "            FROM FILM_DIRECTOR as fd\n" +
                "            WHERE fd.DIRECTOR_ID = ?\n" +
                "            )  \n" +
                "ORDER BY f.RELEASE_DATE;";
        Collection<Film> films = jdbcTemplate.query(sql, FilmDbRepository::makeFilm, id);
        films.forEach(x -> x.setLikes(likesRepository.loadLikes(x.getId())));
        genreRepository.loadFilmGenres(films);
        directorRepository.loadFilmDirectors(films);
        return films;
    }

    @Override
    public Collection<Film> getFilmByDirectorSortedByLikes(Long id) {
        String sql = "SELECT f.*,\n" +
                "       m.NAME MPA_NAME\n" +
                "           FROM FILMS f, MPA m \n" +
                "WHERE f.MPA_ID = m.ID AND (select f.ID from FILMS\n" +
                "    left join LIKES L on FILMS.ID = L.FILM_ID\n" +
                "    WHERE f.ID in (\n" +
                "    SELECT fd.FILM_ID\n" +
                "    FROM FILM_DIRECTOR as fd\n" +
                "    WHERE fd.DIRECTOR_ID = ?\n" +
                "    )\n" +
                "    group by f.ID\n" +
                "    order by count(l.USER_ID)desc)\n" +
                ";";
        Collection<Film> films = jdbcTemplate.query(sql, FilmDbRepository::makeFilm, id);
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
        if (film.getGenres() != null && film.getGenres().size() > 0) {
            genreRepository.saveFilmGenres(List.of(film));
        }
        if (film.getDirectors() != null && film.getDirectors().size() > 0) {
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
        if (countUpdateRows > 0) {
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
        if (films.size() != 1) {
            return null;
        }
        genreRepository.loadFilmGenres(films);
        directorRepository.loadFilmDirectors(films);
        loadLikes(films);
        return films.get(0);
    }

    @Override
    public void loadLikes(Collection<Film> films) {
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

    @Override
    public List<Film> getCommonFilms(long userId, long friendId) {
        String sql = "select f.*, m.name MPA_NAME " +
                "from FILMS f, MPA m, LIKES l1, LIKES l2 " +
                "where f.ID = l1.FILM_ID " +
                "and f.ID = l2.FILM_ID " +
                "and l1.USER_ID = ? " +
                "and l2.USER_ID = ?" +
                "and m.ID = f.MPA_ID";
        return jdbcTemplate.query(sql, FilmDbRepository::makeFilm, userId, friendId);
    }

    private static Film makeFilm(ResultSet rs, int i) throws SQLException {
        return new Film(rs.getLong("ID"),
                rs.getString("NAME"),
                rs.getString("DESCRIPTION"),
                rs.getDate("RELEASE_DATE").toLocalDate(),
                rs.getInt("DURATION"),
                new Mpa(rs.getInt("MPA_ID"), rs.getString("MPA_NAME")),
                new HashSet<>(),
                new ArrayList<>(), new ArrayList<>());
    }

    @Override
    public Collection<Film> getMostPopularFilms(Integer count, Integer genreId, Integer year) {
        String sql = "select f.*, m.NAME MPA_NAME " +
                "from FILMS f LEFT JOIN LIKES l on f.ID = l.FILM_ID " +
                "left join MPA m on f.MPA_ID = M.ID " +
                "group by f.ID order by count(l.USER_ID) desc limit ?";
        if (genreId != null && year != null) {
            sql = "select f.*, m.NAME MPA_NAME " +
                    "from FILMS f LEFT JOIN LIKES l on f.ID = l.FILM_ID " +
                    "left join MPA m on f.MPA_ID = M.ID " +
                    "left join FILM_GENRES fg on f.ID = FG.FILM_ID " +
                    "where fg.GENRE_ID = ? and extract(year from f.RELEASE_DATE) = ? " +
                    "group by f.ID order by count(l.USER_ID) desc limit ?";
            Collection<Film> films = jdbcTemplate.query(sql, FilmDbRepository::makeFilm, genreId, year, count);
            films.forEach(x -> x.setLikes(likesRepository.loadLikes(x.getId())));
            genreRepository.loadFilmGenres(films);
            return films;
        }
        if (genreId != null) {
            sql = "select f.*, m.NAME MPA_NAME " +
                    "from FILMS f LEFT JOIN LIKES l on f.ID = l.FILM_ID " +
                    "left join MPA m on f.MPA_ID = M.ID " +
                    "left join FILM_GENRES fg on f.ID = FG.FILM_ID " +
                    "where fg.GENRE_ID = ? " +
                    "group by f.ID order by count(l.USER_ID) desc limit ?";
            Collection<Film> films = jdbcTemplate.query(sql, FilmDbRepository::makeFilm, genreId, count);
            films.forEach(x -> x.setLikes(likesRepository.loadLikes(x.getId())));
            genreRepository.loadFilmGenres(films);
            return films;
        }
        if (year != null) {
            sql = "select f.*, m.NAME MPA_NAME " +
                    "from FILMS f LEFT JOIN LIKES l on f.ID = l.FILM_ID " +
                    "left join MPA m on f.MPA_ID = M.ID " +
                    "where extract(year from f.RELEASE_DATE) = ? " +
                    "group by f.ID order by count(l.USER_ID) desc limit ?";
            Collection<Film> films = jdbcTemplate.query(sql, FilmDbRepository::makeFilm, year, count);
            films.forEach(x -> x.setLikes(likesRepository.loadLikes(x.getId())));
            genreRepository.loadFilmGenres(films);
            return films;
        }
        Collection<Film> films = jdbcTemplate.query(sql, FilmDbRepository::makeFilm, count);
        films.forEach(x -> x.setLikes(likesRepository.loadLikes(x.getId())));
        genreRepository.loadFilmGenres(films);
        return films;
    }

    @Override
    public Collection<Film> searchFilms(String searchString, String[] searchBy) {
        HashMap<String, String> catToDb = new HashMap<>();
        catToDb.put("director", "d.name");
        catToDb.put("title", "f.name");

        Collection<Film> searchedFilms = new ArrayList<>();

        for (String searchCriteria : searchBy) {
            String query = "SELECT f.*, m.NAME MPA_NAME FROM FILMS f " +
                    "LEFT JOIN MPA m on f.MPA_ID = m.ID " +
                    "LEFT JOIN FILM_DIRECTOR fd on fd.FILM_ID = f.ID " +
                    "LEFT JOIN DIRECTORS d on d.ID = fd.DIRECTOR_ID " +
                    "LEFT JOIN (" +
                    "SELECT l.FILM_ID, count(l.USER_ID) rating FROM LIKES l " +
                    "GROUP BY l.FILM_ID" +
                    ") l on l.FILM_ID = f.ID " +
                    "WHERE " +
                    catToDb.get(searchCriteria) +
                    " ilike \'%" +
                    searchString.toLowerCase() +
                    "%\' " +
                    "ORDER BY l.rating desc";

            searchedFilms.addAll(
                    jdbcTemplate.query(query, FilmDbRepository::makeFilm)
            );
        }

        searchedFilms.forEach(x -> x.setLikes(likesRepository.loadLikes(x.getId())));
        directorRepository.loadFilmDirectors(searchedFilms);
        genreRepository.loadFilmGenres(searchedFilms);

        return searchedFilms.stream()
                .distinct()
                .sorted((v1, v2) -> v2.getLikes().size() - v1.getLikes().size())
                .collect(Collectors.toList());
    }
}