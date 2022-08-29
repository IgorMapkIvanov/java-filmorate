package ru.yandex.practicum.filmorate.dao;

import ru.yandex.practicum.filmorate.model.Film;

import java.util.Collection;
import java.util.List;

public interface FilmRepository {
    Collection<Film> getAll();

    Collection<Film> getFilmByDirectorSortedByYear(Long id);

    Collection<Film> getFilmByDirectorSortedByLikes(Long id);
    Film add(Film film);

    Film update(Film film);

    boolean delete(Long id);

    Film getById(Long id);

    void loadLikes(Collection<Film> films);

    boolean addLike(Long filmId, Long userId);

    boolean deleteLike(Long filmId, Long userId);

    List<Film> getCommonFilms(long userId, long friendId);

    Collection<Film> getMostPopularFilms(Integer count);

    Collection<Film> getMostPopularFilmsByGenreAndYear(Integer count, Integer genreId, Integer year);

    Collection<Film> getMostPopularFilmsByGenre(Integer count, Integer genreId);

    Collection<Film> getMostPopularFilmsByYear(Integer count,Integer year);

    Collection<Film> searchFilms(String searchString, String[] searchBy);

}
