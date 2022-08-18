package ru.yandex.practicum.filmorate.dao;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.annotation.DirtiesContext;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.Mpa;

import java.sql.Date;
import java.time.LocalDate;
import java.util.Collection;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@AutoConfigureTestDatabase
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
@RequiredArgsConstructor(onConstructor_ = @Autowired)
class GenreRepositoryTest {
    private final GenreRepository genreRepo;
    private final JdbcTemplate jdbc;

    @Test
    void shouldGenresRepositoryGetAllTest() {
        Collection<Genre> genreList = genreRepo.getAll();

        assertThat(genreList.size()).isEqualTo(6);
    }

    @Test
    void shouldGenresRepositoryGetByIdTest() {
        Genre comedy =genreRepo.getById(1L);
        Genre drama =genreRepo.getById(2L);
        Genre cartoon =genreRepo.getById(3L);
        Genre thriller =genreRepo.getById(4L);
        Genre documentary =genreRepo.getById(5L);
        Genre action =genreRepo.getById(6L);

        assertThat(comedy).hasFieldOrPropertyWithValue("id", 1);
        assertThat(comedy).hasFieldOrPropertyWithValue("name", "Комедия");
        assertThat(drama).hasFieldOrPropertyWithValue("id", 2);
        assertThat(drama).hasFieldOrPropertyWithValue("name", "Драма");
        assertThat(cartoon).hasFieldOrPropertyWithValue("id", 3);
        assertThat(cartoon).hasFieldOrPropertyWithValue("name", "Мультфильм");
        assertThat(thriller).hasFieldOrPropertyWithValue("id", 4);
        assertThat(thriller).hasFieldOrPropertyWithValue("name", "Триллер");
        assertThat(documentary).hasFieldOrPropertyWithValue("id", 5);
        assertThat(documentary).hasFieldOrPropertyWithValue("name", "Документальный");
        assertThat(action).hasFieldOrPropertyWithValue("id", 6);
        assertThat(action).hasFieldOrPropertyWithValue("name", "Боевик");
    }

    @Test
    void shouldLoadFilmGenres(){
        Film film = new Film(1L,
                "f1",
                "df1",
                LocalDate.of(1999, 6, 12),
                97,
                new Mpa(1, "G"),
                null,
                null,
                null);
        String sqlAddFilm = "INSERT INTO FILMS (NAME, DESCRIPTION,DURATION, RELEASE_DATE, MPA_ID) VALUES (?,?,?,?,?)";
        String sqlAddFilmGenres = "INSERT INTO FILM_GENRES (GENRE_ID, FILM_ID) VALUES (2, 1)";
        jdbc.update(sqlAddFilm, film.getName(),
                film.getDescription(),
                film.getDuration(),
                Date.valueOf(film.getReleaseDate()),
                film.getMpa().getId());
        jdbc.update(sqlAddFilmGenres);

        genreRepo.loadFilmGenres(List.of(film));

        assertThat(film.getGenres()).hasSize(1);
        assertThat(film.getGenres().stream().findAny().get()).hasFieldOrPropertyWithValue("id", 2);
        assertThat(film.getGenres().stream().findAny().get()).hasFieldOrPropertyWithValue("name", "Драма");
    }
}