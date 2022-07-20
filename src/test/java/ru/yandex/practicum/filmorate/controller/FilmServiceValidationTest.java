package ru.yandex.practicum.filmorate.controller;

import org.junit.jupiter.api.Test;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.service.FilmService;
import ru.yandex.practicum.filmorate.storage.InMemoryFilmStorage;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

class FilmServiceValidationTest {


    @Test
    void shouldWhenIdIsNegative() {
        FilmService filmService = new FilmService(new InMemoryFilmStorage());
        Film film = new Film(-1L, "f1", "fd1", LocalDate.of(1995, 12, 28), 130);

        assertThrows(ValidationException.class, () -> filmService.validation(film));
    }

    @Test
    void shouldWhenIdNotFoundInStorage() {
        FilmService filmService = new FilmService(new InMemoryFilmStorage());
        Film film1 = new Film(1L, "f1", "fd1", LocalDate.of(1995, 12, 28), 130);

        assertThrows(ValidationException.class, () -> filmService.validation(film1));
    }

    @Test
    void shouldWhenReLeaseDataIsEarly28121895() {
        FilmService filmService = new FilmService(new InMemoryFilmStorage());
        Film film = new Film(1L, "f1", "fd1", LocalDate.of(1895, 12, 2), 130);

        assertThrows(ValidationException.class, () -> filmService.addFilm(film));
    }

    @Test
    void shouldWhenDurationIsNegative() {
        FilmService filmService = new FilmService(new InMemoryFilmStorage());
        Film film1 = new Film(1L, "f1", "fd1", LocalDate.of(1995, 12, 28), 130);
        filmService.addFilm(film1);
        Film film2 = new Film(1L, "f1", "fd1", LocalDate.of(1995, 12, 27), 0);
        Film film3 = new Film(1L, "f1", "fd1", LocalDate.of(1995, 12, 27), -1);

        assertThrows(ValidationException.class, () -> filmService.validation(film2));
        assertThrows(ValidationException.class, () -> filmService.validation(film3));
    }
}