package ru.yandex.practicum.filmorate.dao;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import ru.yandex.practicum.filmorate.model.Mpa;

import java.util.Collection;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@AutoConfigureTestDatabase
@RequiredArgsConstructor(onConstructor_ = @Autowired)
class MpaRepositoryTest {

    private final MpaRepository mpaRepo;

    @Test
    void shouldMpaRepositoryGetAllTest() {
        Collection<Mpa> mpaList = mpaRepo.getAll();

        assertThat(mpaList.size()).isEqualTo(5);
    }
    @Test
    void shouldMpaRepositoryGetByIdTest() {
        Mpa mpaG =mpaRepo.getById(1L);
        Mpa mpaPG =mpaRepo.getById(2L);
        Mpa mpaPG13 =mpaRepo.getById(3L);
        Mpa mpaR =mpaRepo.getById(4L);
        Mpa mpaNC17 =mpaRepo.getById(5L);

        assertThat(mpaG).hasFieldOrPropertyWithValue("id", 1);
        assertThat(mpaG).hasFieldOrPropertyWithValue("name", "G");
        assertThat(mpaPG).hasFieldOrPropertyWithValue("id", 2);
        assertThat(mpaPG).hasFieldOrPropertyWithValue("name", "PG");
        assertThat(mpaPG13).hasFieldOrPropertyWithValue("id", 3);
        assertThat(mpaPG13).hasFieldOrPropertyWithValue("name", "PG-13");
        assertThat(mpaR).hasFieldOrPropertyWithValue("id", 4);
        assertThat(mpaR).hasFieldOrPropertyWithValue("name", "R");
        assertThat(mpaNC17).hasFieldOrPropertyWithValue("id", 5);
        assertThat(mpaNC17).hasFieldOrPropertyWithValue("name", "NC-17");
    }
}