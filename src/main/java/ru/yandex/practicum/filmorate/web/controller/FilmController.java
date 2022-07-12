package ru.yandex.practicum.filmorate.web.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exceptions.NotFoundException;
import ru.yandex.practicum.filmorate.exceptions.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;

import javax.validation.Valid;
import java.time.LocalDate;
import java.util.*;

@RestController
@RequestMapping(value = "/films")
@Slf4j
public class FilmController {
    private long filmId = 0;
    private final Map<Long, Film> films = new TreeMap<>(Long::compareTo);

    @GetMapping
    public List<Film> getFilms(){
        log.info("Список из {} фильмов отправлен.", films.size());
        return new ArrayList<>(films.values());
    }

    @PostMapping
    public Film addFilm(@Valid @RequestBody Film film){
        film.setId(generateId());
        if(film.getReleaseDate().isBefore(LocalDate.of(1895, 12, 28))){
            log.info("Дата релиза ({}) не болжна быть раньше 28.12.1985 г.", film.getReleaseDate());
            throw new ValidationException("Дата релиза не болжна быть раньше 28.12.1985 г.");
        }
        films.put(film.getId(), film);
        log.info("Новый фильм добавлен: {}", film);
        return films.get(film.getId());
    }

    @PutMapping
    public Film changeFilm(@Valid @RequestBody Film film){
        Optional<Film> optionalFilm = Optional.of(films.get(film.getId()));
        if (optionalFilm.isPresent()) {
            films.put(film.getId(), film);
            log.info("Фильм с id = {} обновлен.", film.getId());
            return films.get(film.getId());
        } else {
            log.info("Не удалось обновить фильм с id = {}, фильм с таким id не найденю", film.getId());
            throw new NotFoundException(String.format("Фильм с id = %s не найден.", film.getId()));
        }
    }

    private long generateId(){
        return filmId = filmId + 1;
    }
}
