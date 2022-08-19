package ru.yandex.practicum.filmorate.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Data;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.annotation.Id;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;

@Data
@Builder
public class Review {
    @Id
    @JsonProperty("reviewId")
    private int id;
    private int filmId;
    private Long userId;
    @JsonProperty("isPositive")
    private Boolean isPositive;
    private String content;
    private int useful;
}
