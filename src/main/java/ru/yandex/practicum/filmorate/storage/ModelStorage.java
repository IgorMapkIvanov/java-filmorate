package ru.yandex.practicum.filmorate.storage;

import ru.yandex.practicum.filmorate.model.ModelType;

import java.util.Collection;
import java.util.Map;
import java.util.Set;

public interface ModelStorage<T extends ModelType >{

    Set<T> getFriends(Long id);

    Collection<T> getAll();

    T add(T model);

    T update(T model);

    Map<Long, T> getStorage();

    void remove(Long id);

    T getById(Long id);
}

