package ru.yandex.practicum.filmorate.web.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exceptions.ValidationException;
import ru.yandex.practicum.filmorate.model.User;

import javax.validation.Valid;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;

@RestController
@RequestMapping(value = "/users")
@Slf4j
public class UserController {

    private Integer id = 0;
    protected final Map<Integer, User> storage = new TreeMap<>();

    private Integer nextId(){
        return ++id;
    }
    @GetMapping
    public Collection<User> getStorage() {
        return storage.values();
    }

    @PostMapping
    public User addData(@Valid @RequestBody User data) {
        data.setId(nextId());
        validation(data);
        storage.put(this.id, data);
        log.info("Add new user into storage. {}", data);
        return data;
    }

    @PutMapping
    public User updateData(@Valid @RequestBody User data) {
        validation(data);
        storage.put(data.getId(), data);
        log.info("User with Id = {} is update.", data.getId());
        return data;
    }

    protected void validation(User data) {
        if (data.getId() <= 0){
            log.info("Uncorrected Id in request: {}}.", data.getId());
            throw new ValidationException(String.format("Uncorrected Id in request: %s.", data.getId()));
        }
        if(!(storage.containsKey(data.getId())) && !data.getId().equals(this.id)){
            log.info("Id in request: {}, not found.", data.getId());
            throw new ValidationException(String.format("Id in request: %s, not found.", data.getId()));
        }
        if(data.getName().isBlank()){
            data.setName(data.getLogin());
        }
        if(data.getBirthday().isAfter(LocalDate.now())){
            log.info("Date of birth cannot be in the future: {}",
                    data.getBirthday().format(DateTimeFormatter.ofPattern("dd.MM.yyyy")));
            throw new ValidationException(String.format("Date of birth cannot be in the future: %s",
                    data.getBirthday().format(DateTimeFormatter.ofPattern("dd.MM.yyyy"))));
        }
    }
}
