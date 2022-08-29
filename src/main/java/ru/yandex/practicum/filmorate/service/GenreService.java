package ru.yandex.practicum.filmorate.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.dao.GenreRepository;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Genre;

import java.util.Collection;

@Service
@Slf4j
public class GenreService {
    private final GenreRepository genreRepository;

    @Autowired
    public GenreService(GenreRepository genreRepository) {
        this.genreRepository = genreRepository;
    }

    public Collection<Genre> getAll() {
        Collection<Genre> allGenres = genreRepository.getAll();
        log.info("SERVICE: Send data of {} genres.", allGenres.size());
        return allGenres;
    }

    public Genre getById(Long id) {
        Genre genre = genreRepository.getById(id);
        if (genre == null){
            log.info("SERVICE: Genre with ID = {}, not found.", id);
            throw new NotFoundException(String.format("Genre with ID = %s, not found.", id));
        }
        log.info("SERVICE: Send data of genre with Id = {}", id);
        return genre;
    }
}
