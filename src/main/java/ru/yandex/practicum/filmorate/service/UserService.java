package ru.yandex.practicum.filmorate.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.ModelStorage;
import ru.yandex.practicum.filmorate.storage.InMemoryUserStorage;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Collection;

@Service
@Slf4j
public class UserService {
    private final ModelStorage<User> userStorage;

    private Long id = 0L;

    @Autowired
    public UserService(InMemoryUserStorage userStorage) {
        this.userStorage = userStorage;
    }

    public Collection<User> getUsers() {
        return userStorage.getModels();
    }

    public User addUser(User user) {
        user.setId(++id);
        validation(user);
        log.info("Add new user into storage. {}", user);
        return userStorage.addModel(user);
    }

    public User updateUser(User user) {
        validation(user);
        log.info("User with Id = {} is update.", user.getId());
        return userStorage.updateModel(user);
    }

    public void validation(User user) {
        if (user.getId() <= 0){
            log.info("Uncorrected Id in request: {}}.", user.getId());
            throw new ValidationException(String.format("Uncorrected Id in request: %s.", user.getId()));
        }
        if(!(userStorage.getStorage().containsKey(user.getId())) && !user.getId().equals(this.id)){
            log.info("Id in request: {}, not found.", user.getId());
            throw new ValidationException(String.format("Id in request: %s, not found.", user.getId()));
        }
        if(user.getName().isBlank()){
            user.setName(user.getLogin());
        }
        if(user.getBirthday().isAfter(LocalDate.now())){
            log.info("Date of birth cannot be in the future: {}",
                    user.getBirthday().format(DateTimeFormatter.ofPattern("dd.MM.yyyy")));
            throw new ValidationException(String.format("Date of birth cannot be in the future: %s",
                    user.getBirthday().format(DateTimeFormatter.ofPattern("dd.MM.yyyy"))));
        }
    }
}
