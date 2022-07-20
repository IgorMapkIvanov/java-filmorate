package ru.yandex.practicum.filmorate.storage;

import lombok.NoArgsConstructor;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Film;

import java.util.Collection;
import java.util.Map;
import java.util.TreeMap;

@Component
@NoArgsConstructor
public class FilmStorage {
    protected final Map<Long, Film> storage = new TreeMap<>();

    public Collection<Film> getFilms() {
        return storage.values();
    }

    public Film addFilm(Film film) {
        storage.put(film.getId(), film);
        return film;
    }

    public Film updateFilm(Film film) {
        if (storage.containsKey(film.getId())){
            storage.remove(film.getId());
            storage.put(film.getId(), film);
        } else {
            throw new NotFoundException(String.format("Film with Id = %s, not found.", film.getId()));
        }
        return film;
    }

    public Map<Long, Film> getStorage() {
        return storage;
    }
}
