package ru.yandex.practicum.filmorate.model;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class Review {
    private int id;
    private int filmId;
    private int userId;
    private boolean isPositive;
    private String content;
}
