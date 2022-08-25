package ru.yandex.practicum.filmorate.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Event;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.service.UserService;

import javax.validation.Valid;
import java.util.*;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping(value = "/users")
public class UserController {

    private final UserService userService;

    // Get requests
    @GetMapping
    public Collection<User> getAll() {
        log.info("CONTROLLER: Request for list of all users.");
        return userService.getUsers();
    }

    @GetMapping("/{id}")
    public User getById(@PathVariable Long id){
        log.info("CONTROLLER: Request for user with ID = {}.", id);
        return userService.getById(id);
    }

    @GetMapping("/{id}/friends")
    public Set<User> getFriends(@PathVariable Long id){
        log.info("CONTROLLER: Request user's friends. User ID = {}.", id);
        return userService.getFriends(id);
    }

    @GetMapping("/{id}/friends/common/{otherId}")
    public Set<User> searchCommonFriends(@PathVariable Long id, @PathVariable Long otherId){
        log.info("CONTROLLER: Request common friends of user with ID = {} and user with ID = {}.", id, otherId);
        return userService.searchCommonFriends(id, otherId);
    }

    @GetMapping("/{id}/recommendations")
    public List<Film> getRecommendations(@Valid @PathVariable Long id) {
        log.info("CONTROLLER: Request for recommendations for user with ID = {}.", id);
        return userService.getRecommendations(id);
    }

    @GetMapping("/{id}/feed")
    public Collection<Event> feed(@PathVariable Long id){
        log.info("CONTROLLER: Request for feed for user with ID = {}.", id);
        return userService.feed(id);
    }

    // Post requests
    @PostMapping
    public User add(@Valid @RequestBody User user) {
        log.info("CONTROLLER: Request to add new user: {}.", user);
        return userService.add(user);
    }

    // Put requests
    @PutMapping
    public User update(@Valid @RequestBody User user) {
        log.info("CONTROLLER: Request to update user with ID = {}.", user.getId());
        return userService.update(user);
    }

    @PutMapping("/{id}/friends/{friendId}")
    public void addFriend(@PathVariable Long id, @PathVariable Long friendId){
        log.info("CONTROLLER: Request user with ID = {} to add friend to user with ID = {}.", id, friendId);
        userService.addFriend(id, friendId);
    }

    //Delete requests
    @DeleteMapping("/{id}")
    public void delete(@PathVariable Long id){
        log.info("CONTROLLER: Request to delete user with ID = {}.", id);
        userService.delete(id);
    }
    @DeleteMapping("/{id}/friends/{friendId}")
    public void deleteFriend(@PathVariable Long id, @PathVariable Long friendId){
        log.info("CONTROLLER: Request user with ID = {} to delete friend to user with ID = {}.", id, friendId);
        userService.deleteFriend(id, friendId);
    }
}
