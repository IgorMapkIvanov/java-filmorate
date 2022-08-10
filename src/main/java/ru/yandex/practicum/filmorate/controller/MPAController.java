package ru.yandex.practicum.filmorate.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.yandex.practicum.filmorate.model.MPA;
import ru.yandex.practicum.filmorate.service.MPAService;

import java.util.Collection;

@RestController
@RequestMapping(value = "/mpa")
public class MPAController {
    private MPAService mpaService;

    @Autowired
    public MPAController(MPAService mpaService) {
        this.mpaService = mpaService;
    }

    // Get requests
    @GetMapping
    public Collection<MPA> getAll() {
        return mpaService.getAll();
    }
    @GetMapping("/{id}")
    public MPA getById(@PathVariable Long id){
        return mpaService.getById(id);
    }
}
