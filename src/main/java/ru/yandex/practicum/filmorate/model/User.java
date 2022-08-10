package ru.yandex.practicum.filmorate.model;

import lombok.*;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

@Getter
@Setter
@ToString
@EqualsAndHashCode(of = "id")
@NoArgsConstructor
@AllArgsConstructor
public class User implements ModelType {
    protected Long id;
    @Email
    protected String email;
    @NotBlank
    @Pattern(regexp = "^\\S*$")
    protected String login;
    protected String name;
    protected LocalDate birthday;
    protected Set<Long> friendsId = new HashSet<>();
}
