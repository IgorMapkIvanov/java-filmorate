package ru.yandex.practicum.filmorate.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.Film;
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
    public Collection<Film> getAll() {
        return filmService.getAll();
    }
    @GetMapping("/{id}")
    public Film getById(@PathVariable Long id){
        return filmService.getById(id);
    }
    @GetMapping("/popular")
    public List<Film> getPopular(@RequestParam(defaultValue = "10") String count){
        return filmService.getPopular(Integer.valueOf(count));
    }

    // Post requests
    @PostMapping
    public Film add(@Valid @RequestBody Film film) {
        return filmService.add(film);
    }

    // Put requests
    @PutMapping
    public Film update(@Valid @RequestBody Film film) {
        return filmService.update(film);
    }
    @PutMapping("/{id}/like/{userId}")
    public void addLike(@PathVariable Long id, @PathVariable Long userId){
        filmService.addLike(id, userId);
    }

    //Delete requests
    @DeleteMapping("/{id}")
    public void remove(@PathVariable Long id){
        filmService.remove(id);
    }
    @DeleteMapping("/{id}/like/{userId}")
    public void removeLike(@PathVariable Long id, @PathVariable Long userId){
        filmService.removeLike(id, userId);
    }
}
