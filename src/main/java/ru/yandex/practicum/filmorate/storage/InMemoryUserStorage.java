package ru.yandex.practicum.filmorate.storage;

import lombok.NoArgsConstructor;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.User;

import java.util.Collection;
import java.util.Map;
import java.util.TreeMap;

@Component
@NoArgsConstructor
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
    public User update(User user) {
        storage.remove(user.getId());
        storage.put(user.getId(), user);
        return user;
    }
}