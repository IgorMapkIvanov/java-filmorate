package ru.yandex.practicum.filmorate.dao;

import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.model.Event;
import ru.yandex.practicum.filmorate.model.EventOperation;
import ru.yandex.practicum.filmorate.model.EventType;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.Collection;

@Repository
@RequiredArgsConstructor
public class EventDbRepository {
    private final JdbcTemplate jdbcTemplate;

    public Collection<Event> getFeedForUser(Long id) {
        String sql = "SELECT * FROM EVENTS WHERE USER_ID = ?";
        return jdbcTemplate.query(sql, this::makeEvent, id);
    }

    private Event makeEvent(ResultSet rs, int rowNum) throws SQLException {
        return Event.builder()
                .eventId(rs.getLong("id"))
                .timestamp(Timestamp.valueOf(rs.getString("event_time")).getTime())
                .userId(rs.getLong("user_id"))
                .eventType(EventType.valueOf(rs.getString("event_type")))
                .operation(EventOperation.valueOf(rs.getString("operation")))
                .entityId(rs.getLong("entity_id"))
                .build();
    }

    public void addEvent(Long userId, Long entityId, EventType eventType, EventOperation operation) {
        String sql = "INSERT INTO EVENTS (EVENT_TIME, USER_ID, EVENT_TYPE, OPERATION, ENTITY_ID) VALUES (?,?,?,?,?)";
        jdbcTemplate.update(sql, Timestamp.valueOf(LocalDateTime.now()),
                userId,
                eventType.name(),
                operation.name(),
                entityId);
    }
}
