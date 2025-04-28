package com.earlybird.ticket.venue.infrastructure.redis.util;

import com.earlybird.ticket.venue.common.dto.RedisReadResult;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.connection.StringRedisConnection;
import org.springframework.data.redis.core.RedisCallback;
import org.springframework.data.redis.core.StringRedisTemplate;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@RequiredArgsConstructor
public abstract class AbstractRedisHashReader<T> {

    private final StringRedisTemplate stringRedisTemplate;

    public List<T> read(List<String> keys) {

        List<Object> results = executePipeline(keys);

        return mapToDtoList(keys, results);
    }

    public RedisReadResult<T> readWithRaw(List<String> keys) {

        List<Object> results = executePipeline(keys);

        List<T> dtoList = mapToDtoList(keys, results);

        Map<String, String> firstMap = (Map<String, String>) results.get(0);
        return new RedisReadResult<>(dtoList, firstMap);
    }

    private List<Object> executePipeline(List<String> keys) {
        List<Object> results = stringRedisTemplate.executePipelined((RedisCallback<Object>) connection -> {
            StringRedisConnection stringConn = (StringRedisConnection) connection;

            for (String key : keys) {
                stringConn.hGetAll(key);
            }
            return null;
        });
        return results;
    }

    private List<T> mapToDtoList(List<String> keys, List<Object> results) {

        List<T> dtoList = new ArrayList<>();

        for (int i = 0; i < results.size(); i++) {

            Map<String, String> map = (Map<String, String>) results.get(i);
            String key = keys.get(i);
            dtoList.add(mapToDto(key, map));
        }

        return dtoList;
    }

    protected abstract T mapToDto(String key, Map<String, String> map);
}
