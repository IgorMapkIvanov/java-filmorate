package ru.yandex.practicum.filmorate.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.Director;
import ru.yandex.practicum.filmorate.service.DirectorService;

import javax.validation.Valid;
import java.util.Collection;

@RestController
@RequiredArgsConstructor
@RequestMapping(value = "/directors")
public class DirectorController {

    private final DirectorService directorService;


    @GetMapping
    public Collection<Director> getAll(){
        return directorService.getAll();
    }

    @GetMapping("/{id}")
    public Director getById(@PathVariable Integer id){
        return directorService.getById(id);
    }

    @PostMapping
    public Director add(@Valid @RequestBody Director director){
        return directorService.add(director);
    }

    @PutMapping
    public Director update(@Valid @RequestBody Director director){
        return directorService.update(director);
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable Integer id){
        directorService.delete(id);
    }
}
