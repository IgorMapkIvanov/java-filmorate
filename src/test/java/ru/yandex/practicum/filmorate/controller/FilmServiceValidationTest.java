package ru.yandex.practicum.filmorate.controller;

import org.junit.jupiter.api.Test;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.service.FilmService;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

class FilmServiceValidationTest {

    @Test
    void shouldWhenReLeaseDataIsEarly28121895() {
        FilmService filmService = new FilmService(null);
        Film film = new Film(1L, "f1", "fd1", LocalDate.of(1895, 12, 2), 130, null);

        assertThrows(ValidationException.class, () -> filmService.validation(film));
    }

    @Test
    void shouldWhenDurationIsNegative() {
        FilmService filmService = new FilmService(null);
        Film film2 = new Film(1L, "f1", "fd1", LocalDate.of(1995, 12, 27), 0, null);
        Film film3 = new Film(1L, "f1", "fd1", LocalDate.of(1995, 12, 27), -1, null);

        assertThrows(ValidationException.class, () -> filmService.validation(film2));
        assertThrows(ValidationException.class, () -> filmService.validation(film3));
    }
}