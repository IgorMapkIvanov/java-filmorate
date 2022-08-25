package ru.yandex.practicum.filmorate.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.service.GenreService;

import java.util.Collection;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping(value = "/genres")
public class GenreController {
    private final GenreService genreService;

    // Get requests
    // Get all genres from DB
    @GetMapping
    public Collection<Genre> getAll() {
        log.info("CONTROLLER: Request for list of all genres.");
        return genreService.getAll();
    }

    // Get genre by ID from DB
    @GetMapping("/{id}")
    public Genre getById(@PathVariable Long id){
        log.info("CONTROLLER: Request for genre with ID = {}.", id);
        return genreService.getById(id);
    }
}
