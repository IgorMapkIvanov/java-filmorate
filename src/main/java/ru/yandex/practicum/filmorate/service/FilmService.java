package ru.yandex.practicum.filmorate.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.storage.ModelStorage;
import ru.yandex.practicum.filmorate.storage.InMemoryFilmStorage;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Collection;

@Service
@Slf4j
public class FilmService {
    private Long id = 0L;

    private final ModelStorage<Film> filmStorage;

    @Autowired
    public FilmService(InMemoryFilmStorage filmStorage) {
        this.filmStorage = filmStorage;
    }

    public Collection<Film> getFilms() {
        return filmStorage.getModels();
    }

    public Film addFilm(Film film) {
        film.setId(++id);
        validation(film);
        log.info("Add new film into storage. {}", film);
        return filmStorage.addModel(film);
    }

    public Film updateFilm(Film film) {
        validation(film);
        log.info("Film with Id = {} is update.", film.getId());
        return filmStorage.updateModel(film);
    }

    public void validation(Film film) {
        if (film.getId() <= 0){
            log.info("Uncorrected Id in request: {}}.", film.getId());
            throw new ValidationException(String.format("Uncorrected Id in request: %s.", film.getId()));
        }
        if(!(filmStorage.getStorage().containsKey(film.getId())) && !film.getId().equals(this.id)){
            log.info("Id in request: {}, not found.", film.getId());
            throw new ValidationException(String.format("Id in request: %s, not found.", film.getId()));
        }
        if (film.getReleaseDate().isBefore(LocalDate.of(1895, 12, 28))){
            log.info("Release data can not early by 28.12.1895. Request release data {}",
                    film.getReleaseDate().format(DateTimeFormatter.ofPattern("dd.MM.yyyy")));
            throw new ValidationException(String.format("Release data can not early by 28.12.1895. Request release data %s",
                    film.getReleaseDate().format(DateTimeFormatter.ofPattern("dd.MM.yyyy"))));
        }
        if(film.getDuration() <= 0){
            log.info("Movie duration must be positive, request duration = {}", film.getDuration());
            throw new ValidationException(String.format("Movie duration must be positive, request duration = %s", film.getDuration()));
        }
    }
}
