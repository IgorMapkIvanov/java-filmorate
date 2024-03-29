package ru.yandex.practicum.filmorate.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.dao.RecommendDbRepository;

import java.util.HashSet;
import java.util.Map;
import java.util.HashMap;
import java.util.Set;

@Component
public class RecommendService {

    private final RecommendDbRepository recommendDbRepository;

    @Autowired
    public RecommendService(RecommendDbRepository recommendDbRepository) {
        this.recommendDbRepository = recommendDbRepository;
    }

    public Set<Long> getRecommendations(long userId) {
        Set<Long> filmSet = new HashSet<>();

        Map<Long, HashMap<Long, Double>> matrixOfDiff = new HashMap<>();    // Матрицы
        Map<Long, Integer> matrixOfFreq = new HashMap<>();
        Map<Long, HashMap<Long, Double>> data = recommendDbRepository.createMatrixOfDiff();
        Map<Long, Double> userLike = data.get(userId);

        /** На основе имеющихся данных мы рассчитаем отношения между элементами, а также количество вхождений элементов.
         *  Для каждого пользователя мы проверяем его/ее рейтинг предметов: */
        for (Map.Entry<Long, HashMap<Long, Double>> entryData : data.entrySet()) {
            if (entryData.getKey() == userId) {
                continue;
            }
            /**На следующем шаге мы проверяем, существует ли элемент в наших матрицах.
             * Если это первый случай, мы создаем новую запись в картах: */
            matrixOfDiff.put(entryData.getKey(), new HashMap<Long, Double>());

            /** На следующем шаге мы собираемся сравнить рейтинги всех элементов: */
            for (Map.Entry<Long, Double> entryData2 : entryData.getValue().entrySet()) {
                double diff = userLike.get(entryData2.getKey()) * entryData2.getValue();
                if (diff == 1.0) {
                    if (!matrixOfFreq.containsKey(entryData.getKey())) {
                        matrixOfFreq.put(entryData.getKey(), 0);
                    }
                    /** Если кто-то уже оценил элемент ранее, мы увеличиваем количество частот на единицу. */
                    int count = matrixOfFreq.get(entryData.getKey());
                    count++;
                    matrixOfFreq.put(entryData.getKey(), count);
                }
                matrixOfDiff.get(entryData.getKey()).put(entryData2.getKey(), diff);
            }

        }

        /** Определяем фильмы, которые один пролайкал, а другой нет. */
        for (Map.Entry<Long, HashMap<Long, Double>> entryData : data.entrySet()) {
            if (matrixOfFreq.get(entryData.getKey()) == null
                    || matrixOfFreq.get(entryData.getKey()) == 0
                    || entryData.getKey() == userId) {
                continue;
            }
            /** Рекомендовать фильмы, которым поставил лайк пользователь с похожими вкусами,
             * а тот, для кого составляется рекомендация, ещё не поставил.: */
            for (Map.Entry<Long, Double> entryData2 : entryData.getValue().entrySet()) {
                Long ed2 = entryData2.getKey();
                Long ed1 = entryData.getKey();
                Double e1 = data.get(ed1).get(ed2);
                Double e2 = matrixOfDiff.get(entryData.getKey()).get(entryData2.getKey());
                if (e1.equals(1.0) && e2.equals(0.0)) {
                    filmSet.add(entryData2.getKey());
                }
            }

        }
        return filmSet;
    }
}
