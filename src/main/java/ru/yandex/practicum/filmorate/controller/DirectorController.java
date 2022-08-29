package ru.yandex.practicum.filmorate.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.Director;
import ru.yandex.practicum.filmorate.service.DirectorService;

import javax.validation.Valid;
import java.util.Collection;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping(value = "/directors")
public class DirectorController {

    private final DirectorService directorService;

    @GetMapping
    public Collection<Director> getAll(){
        log.info("CONTROLLER: Request for list of all directors.");
        return directorService.getAll();
    }

    @GetMapping("/{id}")
    public Director getById(@PathVariable Long id){
        log.info("CONTROLLER: Request for director with ID = {}.", id);
        return directorService.getById(id);
    }

    @PostMapping
    public Director add(@Valid @RequestBody Director director){
        log.info("CONTROLLER: Request to add new director: {}.", director);
        return directorService.add(director);
    }

    @PutMapping
    public Director update(@Valid @RequestBody Director director){
        log.info("CONTROLLER: Request to update director with ID = {}.", director.getId());
        return directorService.update(director);
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable Long id){
        log.info("CONTROLLER: Request to delete director with ID = {}.", id);
        directorService.delete(id);
    }
}
