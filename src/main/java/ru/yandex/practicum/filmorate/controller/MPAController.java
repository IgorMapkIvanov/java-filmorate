package ru.yandex.practicum.filmorate.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.service.MPAService;

import java.util.Collection;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping(value = "/mpa")
public class MPAController {
    private final MPAService mpaService;

    // Get requests
    // Get all Mpa from DB
    @GetMapping
    public Collection<Mpa> getAll() {
        log.info("CONTROLLER: Request for list of all MPA.");
        return mpaService.getAll();
    }

    // Get Mpa by ID from DB
    @GetMapping("/{id}")
    public Mpa getById(@PathVariable Long id){
        log.info("CONTROLLER: Request for MPA with ID = {}.", id);
        return mpaService.getById(id);
    }
}
