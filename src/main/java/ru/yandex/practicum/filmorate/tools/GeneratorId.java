package ru.yandex.practicum.filmorate.tools;

import java.util.Map;

@Deprecated
public class GeneratorId {

    public static long generateId(Map<Long, ?> map) {
        Long currentId  = map.keySet()
                .stream()
                .mapToLong(id -> id)
                .max()
                .orElse(0L);
        return currentId + 1;
    }
}
