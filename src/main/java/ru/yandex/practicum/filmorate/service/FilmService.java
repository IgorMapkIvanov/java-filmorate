package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.dao.GenreRepository;
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
    private final GenreRepository genreRepository;

    public void validation(Film film) {
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

    public Collection<Film> getAll() {
        Collection<Film> films = repository.getAll();
        genreRepository.loadFilmGenres(films);
        log.info("Send data of all films.");
        return films;
    }

    public Film add(Film film) {
        validation(film);
        Film addFilm = repository.add(film);
        genreRepository.saveFilmGenres(List.of(film));
        log.info("Add new film into storage with ID = {}", addFilm.getId());
        return addFilm;
    }

    public Film update(Film film) {
        if(repository.getById(film.getId()) == null) {
            log.info("Movie with ID = {}, not found.", film.getId());
            throw new NotFoundException(String.format("Movie with id = %s, not found", film.getId()));
        }
        repository.update(film);
        genreRepository.saveFilmGenres(List.of(film));
        return film;
    }

    public void delete(Long filmId) {
        if(repository.getById(filmId) == null) {
            log.info("Movie with ID = {}, not found.", filmId);
            throw new NotFoundException(String.format("Movie with id = %s, not found", filmId));
        }
        repository.delete(filmId);
    }

    public Film getById(Long id) {
        Film film = repository.getById(id);
        if (film == null){
            log.info("Film with ID = {}, not found.", id);
            throw new NotFoundException(String.format("Film with ID = %s, not found", id));
        }
        genreRepository.loadFilmGenres(List.of(film));
        repository.loadLikes(List.of(film));
        return film;
    }

    public void addLike(Long filmId, Long userId) {
        if(repository.getById(filmId) == null) {
            log.info("Movie with ID = {}, not found.", filmId);
            throw new NotFoundException(String.format("Movie with id = %s, not found", filmId));
        }
        repository.addLike(filmId, userId);
    }

    public void deleteLike(Long filmId, Long userId) {
        if(repository.getById(filmId) == null) {
            log.info("Movie with ID = {}, not found.", filmId);
            throw new NotFoundException(String.format("Movie with id = %s, not found", filmId));
        }
        repository.deleteLike(filmId, userId);
    }

    public List<Film> getPopular(Integer count) {
        log.info("Send {} popular films", count);
        return repository.getAll().stream()
                .sorted(Comparator.comparing(Film::getLikes, (o1, o2) -> o2.size() - o1.size()))
                .limit(count)
                .collect(Collectors.toUnmodifiableList());
    }
}
