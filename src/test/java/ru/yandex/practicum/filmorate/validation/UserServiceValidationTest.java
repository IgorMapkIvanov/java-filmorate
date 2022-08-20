package ru.yandex.practicum.filmorate.validation;

import org.junit.jupiter.api.Test;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.service.UserService;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

class UserServiceValidationTest {
    @Test
    void shouldWhenNameIsEmpty() {
        UserService userService = new UserService(null, null);
        User user = new User(1L, "u1@ya.ru", "u1", "",LocalDate.of(1995, 12, 28), null);

        userService.validation(user);

        assertEquals(user.getLogin(), user.getName());
    }

    @Test
    void shouldWhenBirthdayInFuture(){
        UserService userService = new UserService(null, null);
        User user = new User(1L, "u1@ya.ru", "u1", "nu1",LocalDate.now().plusDays(1), null);

        assertThrows(ValidationException.class, () -> userService.validation(user));
    }
}