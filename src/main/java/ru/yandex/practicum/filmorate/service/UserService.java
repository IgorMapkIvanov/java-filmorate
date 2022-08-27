package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.dao.EventDbRepository;
import ru.yandex.practicum.filmorate.dao.FilmRepository;
import ru.yandex.practicum.filmorate.dao.UserRepository;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.*;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;

@Service
@Slf4j
@RequiredArgsConstructor
public class UserService {
    private final UserRepository repository;
    private final EventDbRepository eventDbRepository;
    private final FilmRepository filmRepository;
    private final RecommendService recommendService;

    public void validation(User user) {
        if (user.getId() != null && user.getId()<= 0L){
            log.info("VALIDATiON: Incorrect user ID = {}", user.getId());
            throw new NotFoundException(String.format("Incorrect user ID = %s", user.getId()));
        }
        if(user.getName().isBlank()){
            user.setName(user.getLogin());
        }
        if(user.getBirthday().isAfter(LocalDate.now())){
            log.info("VALIDATION: Date of birth cannot be in the future: {}",
                    user.getBirthday().format(DateTimeFormatter.ofPattern("dd.MM.yyyy")));
            throw new ValidationException(String.format("Date of birth cannot be in the future: %s",
                    user.getBirthday().format(DateTimeFormatter.ofPattern("dd.MM.yyyy"))));
        }
    }

    public Collection<User> getUsers() {
        Collection<User> users = repository.getAll();
        log.info("SERVICE: Send data of all users.");
        return users;
    }

    public User add(User user) {
        validation(user);
        User addUser = repository.add(user);
        log.info("SERVICE: Add new user into storage. {}", addUser);
        return addUser;
    }

    public User update(User user) {
        validation(user);
        User updateUser = repository.update(user);
        log.info("SERVICE: User with Id = {} is update.", user.getId());
        return updateUser;
    }

    public User getById(Long id) {
        User user = repository.getById(id);
        if (user == null){
            log.info("SERVICE: User with id = {}, not found", id);
            throw new NotFoundException(String.format("User with id = %s, not found", id));
        }
        log.info("SERVICE: Send user data with id = {}.", id);
        return user;
    }

    public Set<User> getFriends(Long id) {
        if (repository.getById(id) == null){
            log.info("SERVICE: User with id = {}, not found", id);
            throw new NotFoundException(String.format("User with id = %s, not found", id));
        }
        log.info("SERVICE: Send  user's friends with id = {}.", id);
        return repository.getFriends(id);
    }

    public Set<User> searchCommonFriends(Long userId, Long otherId) {
        if (Objects.equals(userId, otherId)){
            log.info("VALIDATION: User IDs must be different. User Id = {}, Friend Id = {}", userId, otherId);
            throw new ValidationException(String.format("User IDs must be different. User Id = %s, Friend Id = %s", userId, otherId));
        }

        twoUserIsExistInDb(userId, otherId);

        log.info("SERVICE: Send common friends users with ID = {} and {}.", userId, otherId);
        return repository.searchCommonFriends(userId, otherId);
    }

    public void addFriend(Long userId, Long friendId) {
        if (Objects.equals(userId, friendId)){
            log.info("SERVICE: User IDs must be different. User Id = {}, Friend Id = {}", userId, friendId);
            throw new ValidationException(String.format("User IDs must be different. User Id = %s, Friend Id = %s", userId, friendId));
        }

        twoUserIsExistInDb(userId, friendId);

        log.info("SERVICE: User with ID = {} add friend with ID = {}.", userId, friendId);
        repository.addFriends(userId, friendId);
        eventDbRepository.addEvent(userId, friendId, EventType.FRIEND, EventOperation.ADD);
    }

    public void deleteFriend(Long userId, Long friendId) {
        if (Objects.equals(userId, friendId)){
            log.info("VALIDATION: User IDs must be different. User Id = {}, Friend Id = {}", userId, friendId);
            throw new ValidationException(String.format("User IDs must be different. User Id = %s, Friend Id = %s", userId, friendId));
        }

        twoUserIsExistInDb(userId, friendId);

        log.info("SERVICE: User with ID = {} delete friend with ID = {}.", userId, friendId);
        repository.deleteFriend(userId, friendId);
        eventDbRepository.addEvent(userId, friendId, EventType.FRIEND, EventOperation.REMOVE);
    }

    public void delete(Long id) {
        boolean isDelete = repository.delete(id);
        if (isDelete){
            log.info("SERVICE: User with ID = {} was delete.", id);
        } else {
            log.info("SERVICE: User with id = {}, not found", id);
            throw new NotFoundException(String.format("User with id = %s, not found", id));
        }
    }

    public List<Film> getRecommendations(Long id) {
        List<Film> films = new ArrayList<>();
        Set<Long> filmsId = recommendService.getRecommendations(id);
        if (!filmsId.isEmpty()) {
            for (Long i : filmsId) {
                films.add(filmRepository.getById(i));
            }
        }
        log.info("SERVICE: Send recommendations for user with ID = {}.", id);
        return films;
    }

    private void twoUserIsExistInDb(Long userId, Long friendId){
        User user = repository.getById(userId);
        User friend = repository.getById(friendId);

        if (user == null){
            if(friend == null){
                log.info("SERVICE: Users with ID = {} and {}, not found", userId, friendId);
                throw new NotFoundException(String.format("Users with ID = %s and %s, not found", userId, friendId));
            } else {
                log.info("SERVICE: User with ID = {}, not found", userId);
                throw new NotFoundException(String.format("User with ID = %s, not found", userId));
            }
        } else if (friend == null){
            log.info("SERVICE: User with ID = {}, not found", friendId);
            throw new NotFoundException(String.format("User with ID = %s, not found", friendId));
        }
    }

    public Collection<Event> getFeedForUser(Long id) {
        Collection<Event> events = eventDbRepository.getFeedForUser(id);
        log.info("SERVICE: Send data of all event by user with ID = {}.", id);
        return events;
    }
}
