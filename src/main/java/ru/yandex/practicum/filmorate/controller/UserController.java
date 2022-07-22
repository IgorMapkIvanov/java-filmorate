package ru.yandex.practicum.filmorate.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
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
    public Collection<User> getUsers() {
        return userService.getUsers();
    }
    @GetMapping("/{id}")
    public User userById(@PathVariable Long id){
        return userService.userById(id);
    }
    @GetMapping("/{id}/friends")
    public Set<User> userFriends(@PathVariable Long id){
        return userService.userFriends(id);
    }
    @GetMapping("/{id}/friends/common/{otherId}")
    public Set<User> searchMutualFriends(@PathVariable Long id, @PathVariable Long otherId){
        return userService.searchMutualFriends(id, otherId);
    }

    // Post requests
    @PostMapping
    public User addUser(@Valid @RequestBody User user) {
        return userService.addUser(user);
    }

    // Put requests
    @PutMapping
    public User updateUser(@Valid @RequestBody User user) {
        return userService.updateUser(user);
    }
    @PutMapping("/{id}/friends/{friendId}")
    public void addFriend(@PathVariable Long id, @PathVariable Long friendId){
        userService.addFriend(id, friendId);
    }

    //Delete requests
    @DeleteMapping("/{id}/friends/{friendId}")
    public void removeFriend(@PathVariable Long id, @PathVariable Long friendId){
        userService.removeFriend(id, friendId);
    }
}
