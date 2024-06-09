package ru.yandex.practicum.filmorate.util;

import java.util.List;
import java.util.function.ToLongFunction;

public class IdGenerator {

    public static <T> long getNextId(List<T> items, ToLongFunction<T> getIdFunction) {
        long currentMaxId = items.stream()
                .mapToLong(getIdFunction)
                .max()
                .orElse(0);
        return currentMaxId + 1;
    }
}
