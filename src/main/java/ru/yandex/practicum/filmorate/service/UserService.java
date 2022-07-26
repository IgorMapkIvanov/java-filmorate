package ru.yandex.practicum.filmorate.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.ModelStorage;
import ru.yandex.practicum.filmorate.storage.InMemoryUserStorage;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@Slf4j
public class UserService {
    private final ModelStorage<User> userStorage;

    private Long id = 0L;

    @Autowired
    public UserService(InMemoryUserStorage userStorage) {
        this.userStorage = userStorage;
    }

    public void validation(User user) {
        if (user.getId() <= 0 ){
            log.info("Uncorrected user Id in request: {}}.", user.getId());
            throw new ValidationException(String.format("Uncorrected user Id in request: %s.", user.getId()));
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

    public Collection<User> getUsers() {
        return userStorage.getAll();
    }

    public User add(User user) {
        user.setId(this.id + 1);
        validation(user);
        this.id++;
        log.info("Add new user into storage. {}", user);
        return userStorage.add(user);
    }

    public User update(User user) {
        if (userStorage.getStorage().containsKey(user.getId())){
            validation(user);
            log.info("User with Id = {} is update.", user.getId());
            return userStorage.update(user);
        } else {
            throw new NotFoundException(String.format("User with id = %s, not found", user.getId()));
        }
    }

    public User getById(Long id) {
        if (userStorage.getStorage().containsKey(id)){
            log.info("Send user data with id = {}.", id);
            return userStorage.getStorage().get(id);
        } else {
            throw new NotFoundException(String.format("User with id = %s, not found", id));
        }
    }

    public Set<User> getFriends(Long id) {
        if (userStorage.getStorage().containsKey(id)){
            log.info("Send  user's friends with id = {}.", id);
            if (userStorage.getStorage().get(id).getFriendsId() != null){
                return userStorage.getStorage().get(id).getFriendsId().stream()
                        .map(x->userStorage.getStorage().get(x))
                        .collect(Collectors.toUnmodifiableSet());
            } else {
                return new HashSet<>();
            }
        } else {
            throw new NotFoundException(String.format("User with id = %s, not found", id));
        }
    }

    public Set<User> searchCommonFriends(Long id, Long otherId) {
        if (userStorage.getStorage().containsKey(id)) {
            if (userStorage.getStorage().containsKey(otherId)) {
                log.info("Send common friends user's with id's: {} and {}", id, otherId);
                if ((userStorage.getStorage().get(id).getFriendsId() != null)
                        && (userStorage.getStorage().get(otherId).getFriendsId() != null)){
                    return userStorage.getStorage().get(id).getFriendsId().stream()
                            .filter(x -> userStorage.getStorage().get(otherId).getFriendsId().contains(x))
                            .map(x -> userStorage.getStorage().get(x))
                            .collect(Collectors.toUnmodifiableSet());
                } else {
                    return new HashSet<>();
                }

            } else {
                throw new NotFoundException(String.format("User with id = %s, not found", otherId));
            }
        } else {
            throw new NotFoundException(String.format("User with id = %s, not found", id));
        }
    }

    public void addFriend(Long id, Long friendId) {
        if (userStorage.getStorage().containsKey(id)) {
            if (userStorage.getStorage().containsKey(friendId)) {
                if (!id.equals(friendId)){
                    log.info("User with Id = {} added User with Id = {} as friend", id, friendId);
                    userStorage.getStorage().get(id).getFriendsId().add(friendId);
                    userStorage.getStorage().get(friendId).getFriendsId().add(id);
                } else {
                    throw new ValidationException(String.format("User IDs must be different. User Id = %s, Friend Id = %s", id, friendId));
                }
            } else {
                throw new NotFoundException(String.format("User with id = %s, not found", friendId));
            }
        } else {
            throw new NotFoundException(String.format("User with id = %s, not found", id));
        }
    }

    public void removeFriend(Long id, Long friendId) {
        if (userStorage.getStorage().containsKey(id)) {
            if (userStorage.getStorage().containsKey(friendId)) {
                if (!id.equals(friendId)){
                    log.info("User with Id = {} added User with Id = {} as friend", id, friendId);
                    userStorage.getStorage().get(id).getFriendsId().remove(friendId);
                    userStorage.getStorage().get(friendId).getFriendsId().remove(id);
                } else {
                    throw new ValidationException(String.format("User IDs must be different. User Id = %s, Friend Id = %s", id, friendId));
                }
            } else {
                throw new NotFoundException(String.format("User with id = %s, not found", friendId));
            }
        } else {
            throw new NotFoundException(String.format("User with id = %s, not found", id));
        }
    }

    public void remove(Long id) {
        if (userStorage.getStorage().containsKey(id)) {
            userStorage.remove(id);
        } else {
            throw new NotFoundException(String.format("User with id = %s, not found", id));
        }
    }
}
