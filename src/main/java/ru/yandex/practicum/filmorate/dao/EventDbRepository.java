package ru.yandex.practicum.filmorate.dao;

import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.model.Event;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.Collection;

@Repository
@RequiredArgsConstructor
public class EventDbRepository {
    private final JdbcTemplate jdbcTemplate;

    public Collection<Event> feed(Long id) {
        String sql = "SELECT * FROM EVENTS WHERE USER_ID = ?";
        return jdbcTemplate.query(sql, EventDbRepository::makeEvent, id);
    }

    private static Event makeEvent(ResultSet rs, int rowNum) throws SQLException {
        return Event.builder()
                .eventId(rs.getLong("id"))
                .timestamp(Timestamp.valueOf(rs.getString("event_time")).getTime())
                .userId(rs.getLong("user_id"))
                .eventType(rs.getString("event_type"))
                .operation(rs.getString("operation"))
                .entityId(rs.getLong("entity_id"))
                .build();
    }

    public void addEvent(Long userId, Long entityId, String eventType, String operation) {
        String sql = "INSERT INTO EVENTS (EVENT_TIME, USER_ID, EVENT_TYPE, OPERATION, ENTITY_ID)\n" +
                "VALUES (?,?,?,?,?)";
        jdbcTemplate.update(sql, Timestamp.valueOf(LocalDateTime.now()),
                userId,
                eventType,
                operation,
                entityId);
    }
}
