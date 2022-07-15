package ru.yandex.practicum.filmorate.web.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exceptions.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;

import javax.validation.Valid;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;

@Slf4j
@RestController
@RequestMapping(value = "/films")
public class FilmController {
    private Integer id = 0;
    protected final Map<Integer, Film> storage = new TreeMap<>();

    private Integer nextId(){
        return ++id;
    }

    @GetMapping
    public Collection<Film> getStorage() {
        log.info("List of {} films sent.", storage.size());
        return storage.values();
    }

    @PostMapping
    public Film addData(@Valid @RequestBody Film data) {
        data.setId(nextId());
        validation(data);
        storage.put(this.id, data);
        log.info("Add new film into storage. {}", data);
        return data;
    }

    @PutMapping
    public Film updateData(@Valid @RequestBody Film data) {
        validation(data);
        storage.put(data.getId(), data);
        log.info("Film with Id = {} is update.", data.getId());
        return data;
    }

    protected void validation(Film data) {
        if (data.getId() <= 0){
            log.info("Uncorrected Id in request: {}}.", data.getId());
            throw new ValidationException(String.format("Uncorrected Id in request: %s.", data.getId()));
        }
        if(!(storage.containsKey(data.getId())) && !data.getId().equals(this.id)){
            log.info("Id in request: {}, not found.", data.getId());
            throw new ValidationException(String.format("Id in request: %s, not found.", data.getId()));
        }
        if (data.getReleaseDate().isBefore(LocalDate.of(1895, 12, 28))){
            log.info("Release data can not early by 28.12.1895. Request release data {}",
                    data.getReleaseDate().format(DateTimeFormatter.ofPattern("dd.MM.yyyy")));
            throw new ValidationException(String.format("Release data can not early by 28.12.1895. Request release data %s",
                    data.getReleaseDate().format(DateTimeFormatter.ofPattern("dd.MM.yyyy"))));
        }
        if(data.getDuration() <= 0){
            log.info("Movie duration must be positive, request duration = {}", data.getDuration());
            throw new ValidationException(String.format("Movie duration must be positive, request duration = %s", data.getDuration()));
        }
    }
}
