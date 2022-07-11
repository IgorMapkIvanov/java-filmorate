package ru.yandex.practicum.filmorate.web.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exceptions.NotFoundException;
import ru.yandex.practicum.filmorate.model.Film;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import java.util.*;

@RestController
@RequestMapping(value = "/films")
@Slf4j
public class FilmController {
    private final Map<Long, Film> films = new HashMap<>();

    @GetMapping
    public List<Film> getFilms(){
        log.info("Список из {} фильмов отправлен.", films.size());
        return new ArrayList<>(films.values());
    }

    @PostMapping
    public Film addFilm(@Valid @RequestBody Film film){
        films.put(film.getId(), film);
        log.info("Новый фильм добавлен: {}", film);
        return films.get(film.getId());
    }

    @PutMapping(value = "/{id}")
    public Film changeFilm(@NotNull @PathVariable("id") Long id, @Valid @RequestBody Film film){
        Optional<Film> optionalFilm = Optional.of(films.get(id));
        if (optionalFilm.isPresent()) {
            films.put(film.getId(), film);
            log.info("Фильм с id = {} обновлен.", id);
            return films.get(film.getId());
        } else {
            log.info("Не удалось обновить фильм с id = {}, фильм с таким id не найденю", id);
            throw new NotFoundException(String.format("Фильм с id = %s не найден.", id));
        }
    }
}
