package ru.yandex.practicum.filmorate.dao;

import ru.yandex.practicum.filmorate.model.Film;

import java.util.Collection;

public interface FilmRepository {
    Collection<Film> getAll();

    Film add(Film film);

    Film update(Film film);

    boolean delete(Long id);

    Film getById(Long id);

    void loadLikes(Collection<Film> films);

    void addLike(Long filmId, Long userId);

    boolean deleteLike(Long filmId, Long userId);
}
