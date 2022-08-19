package ru.yandex.practicum.filmorate.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.dao.DirectorRepository;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Director;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Collection;

@Service
@Slf4j
public class DirectorService {

    private final DirectorRepository directorRepository;

    @Autowired
    public DirectorService(DirectorRepository directorRepository) {
        this.directorRepository = directorRepository;
    }

    public void validation(Director director){
        if (director.getId() != null && director.getId()<= 0){
            log.info("Incorrect director ID = {}", director.getId());
            throw new NotFoundException(String.format("Incorrect director ID = %s", director.getId()));
        }

    }

    public Collection<Director> getAll(){
        Collection<Director> allDirectors = directorRepository.getAll();
        log.info("Send data of {} directors.", allDirectors.size());
        return allDirectors;
    }

    public Director getById(Integer id){
        Director director = directorRepository.getById(id);
        if (director == null){
            log.warn("Director with ID = {}, not found.", id);
            throw new NotFoundException(String.format("Director with ID = %s, not found.", id));
        }
        log.info("Send data of director with Id = {}", id);
        return director;
    }

    public Director add(Director director){
        validation(director);
        Director addDirector = directorRepository.add(director);
        log.info("Add new director into storage. {}", addDirector);
        return director;
    }

    public Director update(Director director){
        if(directorRepository.getById(director.getId()) == null) {
            log.info("Director with ID = {}, not found.", director.getId());
            throw new NotFoundException(String.format("Director with id = %s, not found", director.getId()));
        }
        return directorRepository.update(director);
    }

    public void delete(Integer id){
        boolean isDelete = directorRepository.delete(id);
        if (isDelete){
            log.info("Director with ID = {} was deleted.", id);
        } else {
            log.info("Director with id = {}, not found", id);
            throw new NotFoundException(String.format("Director with id = %s, not found", id));
        }
    }


}
