package ru.yandex.practicum.filmorate.model;


import lombok.Data;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.validation.constraints.*;

import java.time.Duration;
import java.time.LocalDate;


@Entity
@Data
public class Film {

    @Id
    @GeneratedValue
    private long id;
    @NotBlank(message = "название фильма не должно быть пустым")
    private String name;
    @Size(max = 200, message = "описание фильма не должно быть больше 200 символов")
    private String description;
    @NotNull(message = "дата выхода не должна быть пустой")
    @Min(value = -2335564800L, message = "дата выхода фильма не должна быть ранее 28 декабря 1895г")
    private LocalDate releaseDate;
    @NotNull(message = "длительность фильма не должна быть пустой")
    @Positive(message = "длительность фильма должна быть больше 0")
    private Duration duration;
}
