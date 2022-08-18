package ru.yandex.practicum.filmorate.model;

import lombok.*;

import javax.validation.constraints.NotNull;
import java.sql.Timestamp;

@Getter
@Setter
@ToString
@EqualsAndHashCode(of = "id")
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Event {
    @Generated
    protected Long id;
    @NotNull(message = "Event time must not be empty")
    protected Timestamp eventTime;
    @NotNull(message = "User ID must not be empty")
    protected Long userId;
    @NotNull(message = "Type must not be empty")
    protected EventType type;
    @NotNull(message = "Operation must not be empty")
    protected EventOperation operation;
    @NotNull(message = "Entity ID must not be empty")
    protected Long entityId;
}
