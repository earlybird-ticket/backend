package com.earlybird.ticket.venue.infrastructure.redis.util;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;

import java.util.*;

@RequiredArgsConstructor
public abstract class AbstractRedisHashReader<T> {

    private final StringRedisTemplate stringRedisTemplate;

    public List<T> read(List<String> keys) {
        if (keys == null || keys.isEmpty()) {
            return Collections.emptyList();
        }

        List<Object> results = fetchRawData(keys);

        return mapToDtoList(keys, results);
    }

    protected List<T> mapToDtoList(List<String> keys, List<Object> results){
        List<T> dtoList = new ArrayList<>();

        for (int i = 0; i < results.size(); i++) {
            dtoList.add(mapToDto(keys.get(i), results.get(i)));
        }

        return dtoList;
    }

    protected StringRedisTemplate getStringRedisTemplate() {
        return stringRedisTemplate;
    }
    protected abstract List<Object> fetchRawData(List<String> keys);

    protected abstract T mapToDto(String key, Object map);
}
