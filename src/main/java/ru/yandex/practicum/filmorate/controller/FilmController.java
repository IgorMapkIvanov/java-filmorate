package ru.yandex.practicum.filmorate.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.service.FilmService;

import javax.validation.Valid;
import java.util.*;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping(value = "/films")
public class FilmController {
    private final FilmService service;

    // Get requests
    @GetMapping
    public Collection<Film> getAll() {
        return service.getAll();
    }
    @GetMapping("/{id}")
    public Film getById(@PathVariable Long id){
        return service.getById(id);
    }
    @GetMapping("/popular")
    public List<Film> getPopular(@RequestParam(defaultValue = "10") String count){
        return service.getPopular(Integer.valueOf(count));
    }

    @GetMapping("/director/{directorId}")
    public Collection<Film> getFilmByDirectorSorted(@PathVariable Integer directorId, @RequestParam String sortBy){
        return service.getFilmByDirectorSorted(directorId,sortBy);
    }

    // Post requests
    @PostMapping
    public Film add(@Valid @RequestBody Film film) {
        return service.add(film);
    }

    // Put requests
    @PutMapping
    public Film update(@Valid @RequestBody Film film) {
        return service.update(film);
    }
    @PutMapping("/{id}/like/{userId}")
    public void addLike(@PathVariable Long id, @PathVariable Long userId){
        service.addLike(id, userId);
    }

    //Delete requests
    @DeleteMapping("/{id}")
    public void delete(@PathVariable Long id){
        service.delete(id);
    }
    @DeleteMapping("/{id}/like/{userId}")
    public void deleteLike(@PathVariable Long id, @PathVariable Long userId){
        service.deleteLike(id, userId);
    }
}
