package ru.yandex.practicum.filmorate.model;


import lombok.Data;

import javax.validation.constraints.*;

import java.time.LocalDate;


@Data
public class Film {

    private long id;
    @NotBlank(message = "название фильма не должно быть пустым")
    private String name;
    @Size(max = 200, message = "описание фильма не должно быть больше 200 символов")
    private String description;
    @NotNull(message = "дата выхода не должна быть пустой")
    private LocalDate releaseDate;
    @NotNull(message = "длительность фильма не должна быть пустой")
    @Min(value = 1)
    private long duration;
}
