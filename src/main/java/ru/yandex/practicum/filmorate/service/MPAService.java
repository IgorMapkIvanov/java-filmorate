package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.dao.MpaRepository;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Mpa;

import java.util.Collection;

@Service
@Slf4j
@RequiredArgsConstructor
public class MPAService {
    private final MpaRepository mpaRepository;

    public Collection<Mpa> getAll() {
        Collection<Mpa> allMpa = mpaRepository.getAll();
        log.info("SERVICE: Send data of {} MPA.", allMpa.size());
        return allMpa;
    }

    public Mpa getById(Long id) {
        Mpa mpa = mpaRepository.getById(id);
        if (mpa == null){
            log.info("SERVICE: MPA with ID = {}, not found.", id);
            throw new NotFoundException(String.format("MPA with ID = %s, not found.", id));
        }
        log.info("SERVICE: Send data of MPA with Id = {}.", id);
        return mpa;
    }
}