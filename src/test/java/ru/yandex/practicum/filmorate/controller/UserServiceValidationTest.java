package ru.yandex.practicum.filmorate.controller;

import org.junit.jupiter.api.Test;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.service.UserService;
import ru.yandex.practicum.filmorate.storage.UserStorage;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

class UserServiceValidationTest {
    @Test
    void shouldWhenIdIsNegative() {
        UserService userService = new UserService(new UserStorage());
        User user = new User(-1L, "u1@ya.ru", "u1", "un1",LocalDate.of(1995, 12, 28));

        assertThrows(ValidationException.class, () -> userService.validation(user));
    }

    @Test
    void shouldWhenIdNotFoundInStorage() {
        UserService userService = new UserService(new UserStorage());
        User user = new User(2L, "u1@ya.ru", "u1", "un1",LocalDate.of(1995, 12, 28));

        assertThrows(ValidationException.class, () -> userService.validation(user));
    }

    @Test
    void shouldWhenNameIsEmpty() {
        UserService userService = new UserService(new UserStorage());
        User user = new User(1L, "u1@ya.ru", "u1", "",LocalDate.of(1995, 12, 28));
        userService.addUser(user);

        userService.validation(user);

        assertEquals(user.getLogin(), user.getName());
    }

    @Test
    void shouldWhenBirthdayInFuture(){
        UserService userService = new UserService(new UserStorage());
        User user = new User(1L, "u1@ya.ru", "u1", "nu1",LocalDate.now().plusDays(1));

        assertThrows(ValidationException.class, () -> userService.addUser(user));
    }
}