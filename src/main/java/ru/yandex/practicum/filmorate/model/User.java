package ru.yandex.practicum.filmorate.model;

import lombok.*;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import java.time.LocalDate;

@Data
@AllArgsConstructor
public class User {
    protected Integer id;
    @Email
    private String email;
    @NotBlank
    private String login;
    private String name;
    private LocalDate birthday;
}
