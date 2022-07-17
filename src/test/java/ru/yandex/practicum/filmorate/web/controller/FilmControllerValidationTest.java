package ru.yandex.practicum.filmorate.web.controller;

import org.junit.jupiter.api.Test;
import ru.yandex.practicum.filmorate.exceptions.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

class FilmControllerValidationTest {


    @Test
    void shouldWhenIdIsNegative() {
        FilmController filmController = new FilmController();
        Film film = new Film(-1, "f1", "fd1", LocalDate.of(1995, 12, 28), 130);

        assertThrows(ValidationException.class, () -> filmController.validation(film));
    }

    @Test
    void shouldWhenIdNotFoundInStorage() {
        FilmController filmController = new FilmController();
        Film film1 = new Film(1, "f1", "fd1", LocalDate.of(1995, 12, 28), 130);

        assertThrows(ValidationException.class, () -> filmController.validation(film1));
    }

    @Test
    void shouldWhenReLeaseDataIsEarly28121895() {
        FilmController filmController = new FilmController();
        Film film = new Film(1, "f1", "fd1", LocalDate.of(1895, 12, 2), 130);

        assertThrows(ValidationException.class, () -> filmController.addData(film));
    }

    @Test
    void shouldWhenDurationIsNegative() {
        FilmController filmController = new FilmController();
        Film film1 = new Film(1, "f1", "fd1", LocalDate.of(1995, 12, 28), 130);
        filmController.addData(film1);
        Film film2 = new Film(1, "f1", "fd1", LocalDate.of(1995, 12, 27), 0);
        Film film3 = new Film(1, "f1", "fd1", LocalDate.of(1995, 12, 27), -1);

        assertThrows(ValidationException.class, () -> filmController.validation(film2));
        assertThrows(ValidationException.class, () -> filmController.validation(film3));
    }
}