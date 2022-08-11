package ru.yandex.practicum.filmorate.dao;

import ru.yandex.practicum.filmorate.model.User;

import java.util.Collection;
import java.util.Set;

public interface UserRepository {
    Collection<User> getAll();

    User add(User user);

    User update(User user);

    boolean delete(Long id);

    User getById(Long id);

    Set<User> getFriends(Long id);

    void addFriends(Long userId, Long friendId);
    void deleteFriend(Long userId, Long friendId);

    Set<User> searchCommonFriends(Long userId, Long otherId);
}
