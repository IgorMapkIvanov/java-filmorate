package ru.yandex.practicum.filmorate.storage;

import lombok.NoArgsConstructor;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.Film;

import java.util.Collection;
import java.util.Map;
import java.util.TreeMap;

@Component
@NoArgsConstructor
public class InMemoryFilmStorage implements ModelStorage<Film> {
    final Map<Long, Film> storage = new TreeMap<>();

    @Override
    public Map<Long, Film> getStorage() {
        return storage;
    }

    @Override
    public void remove(Long id) {
        storage.remove(id);
    }

    @Override
    public Collection<Film> getAll() {
        return storage.values();
    }

    @Override
    public Film add(Film film) {
        storage.put(film.getId(), film);
        return film;
    }

    @Override
    public Film update(Film film) {
        storage.remove(film.getId());
        storage.put(film.getId(), film);
        return film;
    }
}