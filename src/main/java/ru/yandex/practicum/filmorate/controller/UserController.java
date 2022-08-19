package ru.yandex.practicum.filmorate.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.service.UserService;

import javax.validation.Valid;
import java.util.*;

@RestController
@RequestMapping(value = "/users")
public class UserController {

    private final UserService userService;

    @Autowired
    public UserController(UserService userService) {
        this.userService = userService;
    }

    // Get requests
    @GetMapping
    public Collection<User> getAll() {
        return userService.getUsers();
    }
    @GetMapping("/{id}")
    public User getById(@PathVariable Long id){
        return userService.getById(id);
    }
    @GetMapping("/{id}/friends")
    public Set<User> getFriends(@PathVariable Long id){
        return userService.getFriends(id);
    }
    @GetMapping("/{id}/friends/common/{otherId}")
    public Set<User> searchCommonFriends(@PathVariable Long id, @PathVariable Long otherId){
        return userService.searchCommonFriends(id, otherId);
    }

    @GetMapping("/users/{id}/recommendations")
    public List<Film> getRecommendations(@Valid @PathVariable Long id) {
        return userService.getRecommendations(id);
    }


    // Post requests
    @PostMapping
    public User add(@Valid @RequestBody User user) {
        return userService.add(user);
    }

    // Put requests
    @PutMapping
    public User update(@Valid @RequestBody User user) {
        return userService.update(user);
    }
    @PutMapping("/{id}/friends/{friendId}")
    public void addFriend(@PathVariable Long id, @PathVariable Long friendId){
        userService.addFriend(id, friendId);
    }

    //Delete requests
    @DeleteMapping("/{id}")
    public void delete(@PathVariable Long id){
        userService.delete(id);
    }
    @DeleteMapping("/{id}/friends/{friendId}")
    public void deleteFriend(@PathVariable Long id, @PathVariable Long friendId){
        userService.deleteFriend(id, friendId);
    }
}
