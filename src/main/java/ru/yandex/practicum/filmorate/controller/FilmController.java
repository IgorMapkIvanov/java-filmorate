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
        log.info("CONTROLLER: Request for list of all films.");
        return service.getAll();
    }

    @GetMapping("/{id}")
    public Film getById(@PathVariable Long id) {
        log.info("CONTROLLER: Request for film with ID = {}.", id);
        return service.getById(id);
    }

    @GetMapping("/popular")
    public Collection<Film> getPopular(@RequestParam(value = "count", defaultValue = "10", required = false) Integer count,
                                       @RequestParam(value = "genreId", required = false) Integer genreId,
                                       @RequestParam(value = "year", required = false) Integer year) {
        log.info("CONTROLLER: Request for list of popular films.");
        return service.getPopular(count, genreId, year);
    }

    @GetMapping("/director/{directorId}")
    public Collection<Film> getFilmByDirectorSorted(@PathVariable Long directorId, @RequestParam String sortBy){
        log.info("CONTROLLER: Request for list of films where director with ID = {}.", directorId);
        return service.getFilmByDirectorSorted(directorId,sortBy);
    }

    @GetMapping("/search")
    public Collection<Film> searchFilms(@RequestParam(name = "query") String searchString, @RequestParam(name = "by") String searchBy) {
        log.info("CONTROLLER: Movie search request.");
        return service.searchFilms(searchString, searchBy);
    }

    // Post requests
    @PostMapping
    public Film add(@Valid @RequestBody Film film) {
        log.info("CONTROLLER: Request to add new film: {}.", film);
        return service.add(film);
    }

    // Put requests
    @PutMapping
    public Film update(@Valid @RequestBody Film film) {
        log.info("CONTROLLER: Request to update film with ID = {}.", film.getId());
        return service.update(film);
    }

    @PutMapping("/{id}/like/{userId}")
    public void addLike(@PathVariable Long id, @PathVariable Long userId) {
        log.info("CONTROLLER: Request to add new like: film ID = {}, user ID = {}.", id, userId);
        service.addLike(id, userId);
    }

    //Delete requests
    @DeleteMapping("/{id}")
    public void delete(@PathVariable Long id) {
        log.info("CONTROLLER: Request to delete film with ID = {}.", id);
        service.delete(id);
    }

    @DeleteMapping("/{id}/like/{userId}")
    public void deleteLike(@PathVariable Long id, @PathVariable Long userId) {
        log.info("CONTROLLER: Request to delete like: film ID = {}, user ID = {}.", id, userId);
        service.deleteLike(id, userId);
    }

    @GetMapping("/common")
    public List<Film> getCommonFilms(@RequestParam(value = "userId") long userId,
                                     @RequestParam(value = "friendId") long friendId) {
        log.info("CONTROLLER: Request for list of common films for users with ID = {} and ID = {}.", userId, friendId);
        return service.getCommonFilms(userId, friendId);
    }
}
