package ru.yandex.practicum.filmorate.model;

import lombok.*;

import javax.validation.constraints.NotNull;

@Getter
@Setter
@ToString
@EqualsAndHashCode(of = "eventId")
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Event {
    @Generated
    protected Long eventId;
    @NotNull(message = "Event time must not be empty")
    protected Long timestamp;
    @NotNull(message = "User ID must not be empty")
    protected Long userId;
    @NotNull(message = "Type must not be empty")
    protected EventType eventType;
    @NotNull(message = "Operation must not be empty")
    protected EventOperation operation;
    @NotNull(message = "Entity ID must not be empty")
    protected Long entityId;
}
