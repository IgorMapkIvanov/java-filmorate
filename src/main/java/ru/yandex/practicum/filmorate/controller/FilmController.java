package ru.yandex.practicum.filmorate.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.service.FilmService;

import javax.validation.Valid;
import java.util.*;

@Slf4j
@RestController
@RequestMapping(value = "/films")
public class FilmController {
    private final FilmService filmService;

    @Autowired
    public FilmController(FilmService filmService) {
        this.filmService = filmService;
    }

    // Get requests
    @GetMapping
    public Collection<Film> getFilms() {
        return filmService.getFilms();
    }
    @GetMapping("/{id}")
    public Film userById(@PathVariable Long id){
        return filmService.filmById(id);
    }
    @GetMapping("/popular")
    public Set<Film> getPopularFilms(@RequestParam(defaultValue = "10") Integer count){
        return filmService.getPopularFilms(count);
    }

    // Post requests
    @PostMapping
    public Film addFilm(@Valid @RequestBody Film film) {
        return filmService.addFilm(film);
    }

    // Put requests
    @PutMapping
    public Film updateFilm(@Valid @RequestBody Film film) {
        return filmService.updateFilm(film);
    }
    @PutMapping("/{id}/like/{userId}")
    public void addLike(@PathVariable Long id, @PathVariable Long userId){
        filmService.addLike(id, userId);
    }

    //Delete requests
    @DeleteMapping("/{id}/like/{userId}")
    public void removeLike(@PathVariable Long id, @PathVariable Long userId){
        filmService.removeLike(id, userId);
    }
}
