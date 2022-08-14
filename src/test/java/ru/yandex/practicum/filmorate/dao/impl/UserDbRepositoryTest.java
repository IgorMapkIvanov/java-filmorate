package ru.yandex.practicum.filmorate.dao.impl;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import ru.yandex.practicum.filmorate.dao.UserRepository;
import ru.yandex.practicum.filmorate.model.User;

import java.time.LocalDate;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@AutoConfigureTestDatabase
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
@RequiredArgsConstructor(onConstructor_ = @Autowired)
class UserDbRepositoryTest {
    private final UserRepository userRepo;

    @Test
    void add() {
        User newUser = new User(null,
                "new@ya.ru",
                "new",
                "new",
                LocalDate.now(),
                null);

        assertThat(userRepo.add(newUser).getId()).isEqualTo(3L);
    }

    @Test
    void update() {
        User userNew = new User(2L,
                "u1update@ya.ru",
                "u1update",
                "u1update",
                LocalDate.now(),
                null);

        User updateUser = userRepo.update(userNew);

        assertThat(updateUser).isNotNull();

    }

    @Test
    void delete() {
        assertThat(userRepo.delete(1L)).isTrue();
    }

    @Test
    void getAll() {
        assertThat(userRepo.getAll().size()).isEqualTo(2);
    }

    @Test
    void getById() {
        assertThat(userRepo.getById(2L)).hasFieldOrPropertyWithValue("id", 2L);
    }
}