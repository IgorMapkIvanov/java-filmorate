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

    public Collection<Event> feed(Long id) {
        String sql = "SELECT * FROM EVENTS WHERE USER_ID = ?";
        return jdbcTemplate.query(sql, EventDbRepository::makeEvent, id);
    }



    private static Event makeEvent(ResultSet rs, int rowNum) throws SQLException {
        return Event.builder()
                .id(rs.getLong("id"))
                .eventTime(Timestamp.valueOf(rs.getString("event_time")))
                .userId(rs.getLong("user_id"))
                .type(EventType.valueOf(rs.getString("event_type")))
                .operation(EventOperation.valueOf(rs.getString("operation")))
                .entityId(rs.getLong("entity_id"))
                .build();
    }

    public void addFilmEvent(Long userId, Long filmId, EventType type, EventOperation operation) {
        switch (operation) {
            case ADD:
                addFilmLike(userId, filmId, type, operation);
                break;
            case UPDATE:
                updateFilmLike(userId, filmId, type, operation);
                break;
            case DELETE:
                deleteFilmLike(userId, filmId, type, operation);
                break;
        }
    }

    private void addFilmLike(Long userId, Long filmId, EventType type, EventOperation operation){
        String sql = "INSERT INTO EVENTS (EVENT_TIME, USER_ID, EVENT_TYPE, OPERATION, ENTITY_ID)\n" +
                "VALUES (?,?,?,?,?)";
        jdbcTemplate.update(sql, Timestamp.valueOf(LocalDateTime.now()),
                userId,
                type,
                operation,
                filmId);
    }

    private void updateFilmLike(Long userId, Long filmId, EventType type, EventOperation operation){
        String sql = "MERGE INTO EVENTS (EVENT_TIME, USER_ID, EVENT_TYPE, OPERATION, ENTITY_ID)\n" +
                "VALUES (?,?,?,?,?)";
        jdbcTemplate.update(sql, Timestamp.valueOf(LocalDateTime.now()),
                userId,
                type,
                operation,
                filmId);
    }

    private void deleteFilmLike(Long userId, Long filmId, EventType type, EventOperation operation){
        String sql = "MERGE INTO EVENTS (EVENT_TIME, USER_ID, EVENT_TYPE, OPERATION, ENTITY_ID)\n" +
                "VALUES (?,?,?,?,?)";
        jdbcTemplate.update(sql, Timestamp.valueOf(LocalDateTime.now()),
                userId,
                type,
                operation,
                filmId);
    }
}
