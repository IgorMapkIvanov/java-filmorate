package ru.yandex.practicum.filmorate.web.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exceptions.NotFoundException;
import ru.yandex.practicum.filmorate.exceptions.ValidationException;
import ru.yandex.practicum.filmorate.model.User;

import javax.validation.Valid;
import java.time.LocalDate;
import java.util.*;

@RestController
@RequestMapping(value = "/users")
@Slf4j
public class UserController {
    private long userId = 0;
    private final Map<Long, User> users = new TreeMap<>(Long::compareTo);

    @GetMapping
    public List<User> getFilms(){
        log.info("Список из {} фильмов отправлен.", users.size());
        return new ArrayList<>(users.values());
    }

    @PostMapping
    public User addUser(@Valid @RequestBody User user){
        user.setId(generateId());
        if(user.getBirthday().isAfter(LocalDate.now())){
            log.info("Дата релиза ({}) не болжна быть раньше 28.12.1985 г.", user.getBirthday());
            throw new ValidationException(String.format("Дата рождения не болжна быть позже $s", LocalDate.now()));
        }
        users.put(user.getId(), user);
        log.info("Новый пользователь добавлен: {}", user);
        return users.get(user.getId());
    }

    @PutMapping
    public User changeUser(@Valid @RequestBody User user){
        Optional<User> optionalUser = Optional.of(users.get(user.getId()));
        if (optionalUser.isPresent()) {
            users.put(user.getId(), user);
            log.info("Пользователь с id = {} обновлен.", user.getId());
            return users.get(user.getId());
        } else {
            log.info("Не удалось обновить пользователя с id = {}, фильм с таким id не найденю", user.getId());
            throw new NotFoundException(String.format("Пользователь с id = %s не найден.", user.getId()));
        }
    }

    private long generateId(){
        return userId = userId + 1;
    }
}
