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

    public Collection<User> getModels() {
        return storage.values();
    }

    public User addModel(User user) {
        storage.put(user.getId(), user);
        return user;
    }

    public Map<Long, User> getStorage() {
        return storage;
    }

    public User updateModel(User user) {
        if (storage.containsKey(user.getId())){
            storage.remove(user.getId());
            storage.put(user.getId(), user);
        } else {
            throw new NotFoundException(String.format("User with Id = %s, not found.", user.getId()));
        }
        return user;
    }
}