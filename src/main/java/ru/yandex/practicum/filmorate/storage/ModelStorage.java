package ru.yandex.practicum.filmorate.storage;

import ru.yandex.practicum.filmorate.model.ModelType;

import java.util.Collection;
import java.util.Map;

public interface ModelStorage<T extends ModelType >{
    Collection<T> getModels();

    T addModel(T model);

    T updateModel(T model);

    Map<Long, T> getStorage();
}
