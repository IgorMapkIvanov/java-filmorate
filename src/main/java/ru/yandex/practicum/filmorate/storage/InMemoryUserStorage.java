package ru.yandex.practicum.filmorate.storage;

import lombok.NoArgsConstructor;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.User;

import java.util.Collection;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

@Component
@NoArgsConstructor
@Qualifier("InMemoryUserStorage")
public class InMemoryUserStorage implements ModelStorage<User>{
    protected final Map<Long, User> storage = new TreeMap<>();

    @Override
    public Collection<User> getAll() {
        return storage.values();
    }

    @Override
    public User add(User user) {
        storage.put(user.getId(), user);
        return user;
    }

    @Override
    public Map<Long, User> getStorage() {
        return storage;
    }

    @Override
    public void remove(Long id) {
        storage.remove(id);
    }

    @Override
    public User getById(Long id) {
        return storage.getOrDefault(id, null);
    }

    @Override
    public Set<User> getFriends(Long id) {
        return null;
    }

    @Override
    public User update(User user) {
        storage.remove(user.getId());
        storage.put(user.getId(), user);
        return user;
    }
}