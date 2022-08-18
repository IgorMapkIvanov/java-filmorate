package ru.yandex.practicum.filmorate.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Data;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.annotation.Id;

@Data
@Builder
public class Review {
    @Id
    @JsonProperty("reviewId")
    private int id;
    private int filmId;
    private int userId;
    @JsonProperty("isPositive")
    private Boolean isPositive;
    private String content;
    private int useful;
}
