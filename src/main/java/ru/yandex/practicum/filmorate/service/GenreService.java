package ru.yandex.practicum.filmorate.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.dao.impl.DaoGenre;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Genre;

import java.util.Collection;

@Service
@Slf4j
public class GenreService {
    private final DaoGenre daoGenre;

    @Autowired
    public GenreService(DaoGenre daoGenre) {
        this.daoGenre = daoGenre;
    }

    public Collection<Genre> getAll() {
        log.info("Send data of all genres.");
        return daoGenre.getAll();
    }

    public Genre getById(Long id) {
        try {
            Genre genre = daoGenre.getById(id);
            log.info("Send data of genre with Id = {}", id);
            return genre;
        } catch (EmptyResultDataAccessException e){
            log.info("Genre with ID = {}, not found.", id);
            throw new NotFoundException(String.format("Genre with ID = %s, not found.", id));
        }
    }
}
