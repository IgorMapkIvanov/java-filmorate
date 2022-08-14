package ru.yandex.practicum.filmorate.dao.impl;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import ru.yandex.practicum.filmorate.dao.FilmRepository;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Mpa;

import java.time.LocalDate;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@AutoConfigureTestDatabase
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
@RequiredArgsConstructor(onConstructor_ = @Autowired)
class FilmDbRepositoryTest {
    private final FilmRepository filmRepo;

    @Test
    void getAll() {
        assertThat(filmRepo.getAll().size()).isEqualTo(2);
    }

    @Test
    void add() {
        Film newFilm = new Film(null,
                "f4",
                "f4",
                LocalDate.of(1967, 2, 4),
                100,
                new Mpa(2, "PG"),
                null,
                null);

        assertThat(filmRepo.add(newFilm).getId()).isEqualTo(3L);
    }

    @Test
    void update() {
        Film newFilm = new Film(2L,
                "updateFilm",
                "updateFilm",
                LocalDate.of(1967, 4, 4),
                100,
                new Mpa(2, "PG"),
                null,
                null);

        Film updateFilm = filmRepo.update(newFilm);

        assertThat(updateFilm).hasFieldOrPropertyWithValue("name", "updateFilm");
    }

    @Test
    void delete() {
        assertThat(filmRepo.delete(1L)).isTrue();
    }

    @Test
    void getById() {
        assertThat(filmRepo.getById(2L)).hasFieldOrPropertyWithValue("id", 2L);
    }
}