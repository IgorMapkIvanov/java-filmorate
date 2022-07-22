package ru.yandex.practicum.filmorate.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.storage.ModelStorage;
import ru.yandex.practicum.filmorate.storage.InMemoryFilmStorage;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Collection;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

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
        film.setId(++this.id);
        validation(film);
        log.info("Add new film into storage. {}", film);
        return filmStorage.addModel(film);
    }

    public Film updateFilm(Film film) {
        if (filmStorage.getStorage().containsKey(film.getId())){
            validation(film);
            log.info("Film with Id = {} is update.", film.getId());
            return filmStorage.updateModel(film);
        } else {
            throw new NotFoundException(String.format("Film with id = %s, not found", film.getId()));
        }
    }

    public void validation(Film film) {
        if (film.getId() <= 0){
            log.info("Uncorrected film Id in request: {}}.", film.getId());
            throw new ValidationException(String.format("Uncorrected film Id in request: %s.", film.getId()));
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
//        Long id = this.id + 1;
//        boolean contain = filmStorage.getStorage().containsKey(film.getId());
//        boolean equal = film.getId() != (id);
//        if((!contain) && equal){
//            log.info("Film with Id in request: {}, not found.", film.getId());
//            throw new NotFoundException(String.format("Film with Id in request: %s, not found.", film.getId()));
//        }
    }

    public Film filmById(Long id) {
        if (filmStorage.getStorage().containsKey(id)){
            log.info("Send user data with id = {}.", id);
            return filmStorage.getStorage().get(id);
        } else {
            throw new NotFoundException(String.format("Film with id = %s, not found", id));
        }
    }

    public void addLike(Long id, Long userId) {
        if (filmStorage.getStorage().containsKey(id)){
            log.info("Movie with Id = {} was liked by the user with Id = {}.", id, userId);
            filmStorage.getStorage().get(id).getLikes().add(userId);
        } else {
            throw new NotFoundException(String.format("Movie with id = %s, not found", id));
        }
    }

    public void removeLike(Long id, Long userId) {
        if (userId <= 0){
            throw new NotFoundException(String.format("User id must be positive: id = %s.", id));
        }
        if (filmStorage.getStorage().containsKey(id)){
            log.info("Movie with Id = {} was disliked by the user with Id = {}.", id, userId);
            filmStorage.getStorage().get(id).getLikes().remove(userId);
        } else {
            throw new NotFoundException(String.format("Movie with id = %s, not found", id));
        }
    }

    public List<Film> getPopularFilms(Integer count) {
        log.info("Send {} popular films", count);
        return filmStorage.getModels().stream()
                .sorted(Comparator.comparing(Film::getLikes, (o1, o2) -> o2.size() - o1.size()))
                .limit(count)
                .collect(Collectors.toUnmodifiableList());
    }
}
