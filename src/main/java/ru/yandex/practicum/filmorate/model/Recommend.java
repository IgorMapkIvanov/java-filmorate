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
    public Set<Long> getRecommendations(Long userId) {
        Map<Long, HashMap<Long, Double>> matrixOfDiff = new HashMap<>();    // Матрицы
        Map<Long, HashMap<Long, Integer>> matrixOfFreq = new HashMap<>();
        Map<Long, HashMap<Long, Double>> data = createMatrixOfDiff();
        Map<Long, Double> userLike = data.get(userId);
        Set<Long> filmSet = new HashSet<>();

        /**На основе имеющихся данных мы рассчитаем отношения между элементами, а также количество вхождений элементов.
         * Для каждого пользователя мы проверяем его/ее рейтинг предметов:
         for (HashMap<Item, Double> user : data.values()) {
         for (Entry<Item, Double> e : user.entrySet()) {    <---
         // ... }} */
        for (Map.Entry<Long, HashMap<Long, Double>> entryData : data.entrySet()) {
            /** На следующем шаге мы проверяем, существует ли элемент в наших матрицах.
             * Если это первый случай, мы создаем новую запись в Map:
             * if (!diff.containsKey(e.getKey())) {
             *     diff.put(e.getKey(), new HashMap<Item, Double>());
             *     freq.put(e.getKey(), new HashMap<Item, Integer>());
             * }
             */
            if (!matrixOfDiff.containsKey(entryData.getKey())) {
                matrixOfDiff.put(entryData.getKey(), new HashMap<Long, Double>());
                /** На следующем шаге мы собираемся сравнить рейтинги всех элементов:
                 * for (Entry<Item, Double> e2 : user.entrySet()) {                         // УДАЛИТЬ !!!
                 *     int oldCount = 0;
                 *     if (freq.get(e.getKey()).containsKey(e2.getKey())){
                 *         oldCount = freq.get(e.getKey()).get(e2.getKey()).intValue();
                 *     }
                 *
                 *     double oldDiff = 0.0;
                 *     if (diff.get(e.getKey()).containsKey(e2.getKey())){
                 *         oldDiff = diff.get(e.getKey()).get(e2.getKey()).doubleValue();
                 *     }
                 *
                 *     double observedDiff = e.getValue() - e2.getValue();
                 *     freq.get(e.getKey()).put(e2.getKey(), oldCount + 1);
                 *     diff.get(e.getKey()).put(e2.getKey(), oldDiff + observedDiff);       // УДАЛИТЬ !!!
                 * } */
                for (Map.Entry<Long, Double> entryData2 : entryData.getValue().entrySet()) {
                    int oldCount = 0;
                    if (matrixOfFreq.get(entryData.getKey()).containsKey(entryData2.getKey())) {
                        oldCount = matrixOfFreq.get(entryData.getKey()).get(entryData2.getKey());
                    }

                    double oldDiff = 0.0;
                    if (matrixOfDiff.get(entryData.getKey()).containsKey(entryData2.getKey())) {
                        oldDiff = matrixOfDiff.get(entryData.getKey()).get(entryData2.getKey());
                    }

                    double observedDiff = entryData.getKey() - entryData2.getKey();
        //ИЛИ ТАК?  double observedDiff = userLike.get(entryData.getKey() - entryData2.getKey());
                    matrixOfFreq.get(entryData.getKey()).put(entryData2.getKey(), oldCount +1);
                    matrixOfDiff.get(entryData.getKey()).put(entryData2.getKey(), oldDiff + observedDiff);
//                    double diff = userLike.get(entryData2.getKey()) * entryData2.getValue();
//                    if (diff == 1.0) {
//                        if (!matrixOfFreq.containsKey(entryData.getKey())) {
//                            matrixOfFreq.put(entryData.getKey(), 0);
//                        }
//                        int oldCount = matrixOfFreq.get(entryData2.getKey());
//                        oldCount += 1;
//                        matrixOfFreq.put(entryData.getKey(), oldCount);
//                    }
//                    matrixOfDiff.get(entryData2.getKey()).put(entryData2.getKey(), diff);
                }
            }
        }
//        for (Map.Entry<Long, HashMap<Long, Double>> entryData : data.entrySet()) {
//            if (matrixOfFreq.get(entryData.getKey()) == null
//                    || matrixOfFreq.get(entryData.getKey()) == 0
//                    || entryData.getKey() == userId) {
//
//                for (Map.Entry<Long, Double> entryData2 : entryData.getValue().entrySet()) {
//                    if (data.get(entryData.getKey()).get(entryData2.getKey()) == 1
//                            && matrixOfDiff.get(entryData.getKey()).get(entryData2.getKey()) == 0) {
//                        filmSet.add(entryData2.getKey());
//                    }
//                }
//            }
//        }

        /** Наконец, мы вычисляем оценки сходства внутри матриц:
         * for (Item j : diff.keySet()) {
         *     for (Item i : diff.get(j).keySet()) {
         *         double oldValue = diff.get(j).get(i).doubleValue();
         *         int count = freq.get(j).get(i).intValue();
         *         diff.get(j).put(i, oldValue / count);
         *     }
         * }*/
        for (Long j : matrixOfDiff.keySet()) {
            for (Long i : matrixOfDiff.get(j).keySet()) {
                double oldValue = matrixOfDiff.get(j).get(i);
                int count = matrixOfFreq.get(j).get(i);
                matrixOfDiff.get(j).put(i, oldValue / count);
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
            films.put(filmRows.getLong("user_id"), 0.0);
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
