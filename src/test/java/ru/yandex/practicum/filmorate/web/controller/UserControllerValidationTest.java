package ru.yandex.practicum.filmorate.web.controller;

import org.junit.jupiter.api.Test;
import ru.yandex.practicum.filmorate.exceptions.ValidationException;
import ru.yandex.practicum.filmorate.model.User;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

class UserControllerValidationTest {
    @Test
    void shouldWhenIdIsNegative() {
        UserController userController = new UserController();
        User user = new User(-1, "u1@ya.ru", "u1", "un1",LocalDate.of(1995, 12, 28));

        assertThrows(ValidationException.class, () -> userController.validation(user));
    }

    @Test
    void shouldWhenIdNotFoundInStorage() {
        UserController userController = new UserController();
        User user = new User(2, "u1@ya.ru", "u1", "un1",LocalDate.of(1995, 12, 28));

        assertThrows(ValidationException.class, () -> userController.validation(user));
    }

    @Test
    void shouldWhenNameIsEmpty() {
        UserController userController = new UserController();
        User user = new User(1, "u1@ya.ru", "u1", "",LocalDate.of(1995, 12, 28));
        userController.addData(user);

        userController.validation(user);

        assertEquals(user.getLogin(), user.getName());
    }

    @Test
    void shouldWhenBirthdayInFuture(){
        UserController userController = new UserController();
        User user = new User(1, "u1@ya.ru", "u1", "nu1",LocalDate.now().plusDays(1));

        assertThrows(ValidationException.class, () -> userController.addData(user));
    }
}