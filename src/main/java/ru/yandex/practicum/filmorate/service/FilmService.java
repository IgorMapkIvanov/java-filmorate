package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestParam;
import ru.yandex.practicum.filmorate.dao.DirectorRepository;
import ru.yandex.practicum.filmorate.dao.EventDbRepository;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.dao.FilmRepository;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
public class FilmService {

    private final FilmRepository repository;
    private final EventDbRepository eventDbRepository;
    private final DirectorRepository directorRepository;

    public void validation(Film film) {
        if (film.getId() != null && film.getId() <= 0L) {
            log.info("Incorrect film ID = {}", film.getId());
            throw new NotFoundException(String.format("Incorrect film ID = %s", film.getId()));
        }
        if (film.getReleaseDate().isBefore(LocalDate.of(1895, 12, 28))) {
            log.info("Release data can not early by 28.12.1895. Request release data {}",
                    film.getReleaseDate().format(DateTimeFormatter.ofPattern("dd.MM.yyyy")));
            throw new ValidationException(String.format("Release data can not early by 28.12.1895. Request release data %s",
                    film.getReleaseDate().format(DateTimeFormatter.ofPattern("dd.MM.yyyy"))));
        }
        if (film.getDuration() <= 0) {
            log.info("Movie duration must be positive, request duration = {}", film.getDuration());
            throw new ValidationException(String.format("Movie duration must be positive, request duration = %s", film.getDuration()));
        }
    }

    public Collection<Film> searchFilms(String searchString, String searchBy) {
        return repository.searchFilms(searchString, searchBy.split(","));
    }

    public Collection<Film> getAll() {
        Collection<Film> films = repository.getAll();
        log.info("Send data of all films.");
        return films;
    }


    public Collection<Film> getFilmByDirectorSorted(Integer id, String sort){
        if(directorRepository.getById(id) == null){
            log.warn("Director with ID = {}, not found.", id);
            throw new NotFoundException(String.format("Director with id = %s, not found", id));
        }
        Collection<Film> films = repository.getFilmByDirectorSorted(id,sort);
        log.info("Send data of all films.");
        return films;
    }
    public Film add(Film film) {
        validation(film);
        Film addFilm = repository.add(film);
        log.info("Add new film into storage with ID = {}", addFilm.getId());
        return addFilm;
    }

    public Film update(Film film) {
        if (repository.getById(film.getId()) == null) {
            log.info("Movie with ID = {}, not found.", film.getId());
            throw new NotFoundException(String.format("Movie with id = %s, not found", film.getId()));
        }
        repository.update(film);
        return film;
    }

    public void delete(Long filmId) {
        if (repository.getById(filmId) == null) {
            log.info("Movie with ID = {}, not found.", filmId);
            throw new NotFoundException(String.format("Movie with id = %s, not found", filmId));
        }
        repository.delete(filmId);
    }

    public Film getById(Long id) {
        Film film = repository.getById(id);
        if (film == null) {
            log.info("Film with ID = {}, not found.", id);
            throw new NotFoundException(String.format("Film with ID = %s, not found", id));
        }
        return film;
    }

    public void addLike(Long filmId, Long userId) {
        if (repository.getById(filmId) == null) {
            log.info("Movie with ID = {}, not found.", filmId);
            throw new NotFoundException(String.format("Movie with id = %s, not found", filmId));
        }
        if(repository.addLike(filmId, userId)){
            eventDbRepository.addEvent(userId, filmId, "LIKE", "ADD");
        }
    }

    public void deleteLike(Long filmId, Long userId) {
        if (repository.getById(filmId) == null) {
            log.info("Movie with ID = {}, not found.", filmId);
            throw new NotFoundException(String.format("Movie with id = %s, not found", filmId));
        }
        if (!repository.deleteLike(filmId, userId)){
            log.info("User with ID = {}, don't like film with ID = {}.",userId, filmId);
            throw new NotFoundException(String.format("User with ID = %s, don't like film with ID = %s.",userId, filmId));
        } else {
            eventDbRepository.addEvent(userId, filmId, "LIKE", "REMOVE");
        }
    }

    public Collection<Film> getPopular(Integer count, Integer genreId, Integer year) {
        log.info("Send {} popular films", count);
        return repository.getMostPopularFilms(count, genreId, year);
    }

    public List<Film> getCommonFilms(long userId, long friendId) {
        return repository.getCommonFilms(userId, friendId);
    }
}
