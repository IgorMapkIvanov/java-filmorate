package ru.yandex.practicum.filmorate.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.dao.impl.DaoMPA;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.MPA;

import java.util.Collection;

@Service
@Slf4j
public class MPAService {
    private final DaoMPA daoMPA;

    @Autowired
    public MPAService(DaoMPA daoMPA) {
        this.daoMPA = daoMPA;
    }

    public Collection<MPA> getAll() {
        log.info("Send data of all MPA.");
        return daoMPA.getAll();
    }

    public MPA getById(Long id) {
        try {
            MPA mpa = daoMPA.getById(id);
            log.info("Send data of MPA with Id = {}.", id);
            return mpa;
        } catch (EmptyResultDataAccessException e){
            log.info("MPA with ID = {}, not found.", id);
            throw new NotFoundException(String.format("MPA with ID = %s, not found.", id));
        }
    }
}
