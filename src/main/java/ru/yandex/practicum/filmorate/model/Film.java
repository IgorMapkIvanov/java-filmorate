package ru.yandex.practicum.filmorate.model;


import lombok.*;

import javax.validation.constraints.*;

import java.time.LocalDate;

@Data
@AllArgsConstructor
public class Film {
    protected Integer id;
    @NotBlank(message = "movie title cannot be empty")
    protected String name;
    @Size(max = 200, message = "Movie description must not exceed 200 characters")
    protected String description;
    @NotNull(message = "movie release date must not be empty")
    protected LocalDate releaseDate;
    protected Integer duration;
}
