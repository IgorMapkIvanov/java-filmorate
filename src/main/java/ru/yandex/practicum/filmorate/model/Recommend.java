package ru.yandex.practicum.filmorate.model;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Component;

import java.util.HashSet;
import java.util.Map;
import java.util.HashMap;
import java.util.Set;

@Component
public class Recommend {

    private final JdbcTemplate jdbcTemplate;

    public Recommend(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    /**
     * По шкале от 0,0 до 1,0, где 0 означает отсутствие интереса и 1,0 — полный интерес.
     * В результате инициализации данных мы получим Map с данными ранжирования пользовательских элементов:
     * Map<User, HashMap<Item, Double>> data;
     */
    public Set<Long> getRecommendations(long userId) {
        Set<Long> filmSet = new HashSet<>();
        Map<Long, HashMap<Long, Double>> matrixOfDiff = new HashMap<>();    // Матрицы
        Map<Long, Integer> matrixOfFreq = new HashMap<>();
        Map<Long, HashMap<Long, Double>> data = createMatrixOfDiff();
        Map<Long, Double> userLike = data.get(userId);

        for (Map.Entry<Long, HashMap<Long, Double>> entryData : data.entrySet()) {
            if (entryData.getKey() == userId) {
                matrixOfDiff.put(entryData.getKey(), new HashMap<Long, Double>());
                for (Map.Entry<Long, Double> entryData2 : entryData.getValue().entrySet()) {
                    double diff = userLike.get(entryData2.getKey()) * entryData2.getValue();
                    if (diff == 1.0) {
                        if (!matrixOfFreq.containsKey(entryData.getKey())) {
                            matrixOfFreq.put(entryData.getKey(), 0);
                        }
                        int count = matrixOfFreq.get(entryData.getKey());
                        count += 1;
                        matrixOfFreq.put(entryData.getKey(), count);
                    }
                    matrixOfDiff.get(entryData.getKey()).put(entryData2.getKey(), diff);
                }
            }
        }

        for (Map.Entry<Long, HashMap<Long, Double>> entryData : data.entrySet()) {
            if (matrixOfFreq.get(entryData.getKey()) == null
                    || matrixOfFreq.get(entryData.getKey()) == 0
                    || entryData.getKey() == userId) {

                for (Map.Entry<Long, Double> entryData2 : entryData.getValue().entrySet()) {
                    if (data.get(entryData.getKey()).get(entryData2.getKey()) == 1
                            && matrixOfDiff.get(entryData.getKey()).get(entryData2.getKey()) == 0) {
                        filmSet.add(entryData2.getKey());
                    }
                }
            }
        }
        return filmSet;
    }

    private Map<Long, HashMap<Long, Double>> createMatrixOfDiff() {
        Map<Long, HashMap<Long, Double>> data = new HashMap<>();
        Map<Long, Double> films = new HashMap<>();

        SqlRowSet likeRows = jdbcTemplate.queryForRowSet("SELECT * FROM Likes");
        SqlRowSet filmRows = jdbcTemplate.queryForRowSet("SELECT film_id FROM Likes GROUP BY film_id");

        while (filmRows.next()) {
            films.put(filmRows.getLong("film_id"), 0.0);
        }
        while (likeRows.next()) {
            if(!data.containsKey(likeRows.getLong("user_id"))) {
                data.put(likeRows.getLong("user_id"), new HashMap<>(films));
            }
            data.get(likeRows.getLong("user_id")).put(likeRows.getLong("film_id"), 1.0);
        }
        return data;
    }

}