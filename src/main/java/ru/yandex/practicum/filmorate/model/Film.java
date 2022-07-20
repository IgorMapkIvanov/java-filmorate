package ru.yandex.practicum.filmorate.model;


import lombok.*;

import javax.validation.constraints.*;

import java.time.LocalDate;

@Getter
@Setter
@ToString
@EqualsAndHashCode
@NoArgsConstructor
@AllArgsConstructor
public class Film implements ModelType{
    protected Long id;
    @NotBlank(message = "Movie title cannot be empty")
    protected String name;
    @Size(max = 200, message = "Movie description must not exceed 200 characters")
    protected String description;
    @NotNull(message = "Movie release date must not be empty")
    protected LocalDate releaseDate;
    protected Integer duration;
}
