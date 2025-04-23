package com.earlybird.ticket.venue.common.util;

import com.earlybird.ticket.venue.common.exception.RedisException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.connection.RedisConnection;
import org.springframework.data.redis.core.Cursor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ScanOptions;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
@RequiredArgsConstructor
public class RedisKeyScanner {
    private final RedisTemplate<String, String> stringRedisTemplate;

    public List<String> scanKeys(String pattern, long count) {
        List<String> keys = new ArrayList<>();

        ScanOptions options = ScanOptions.scanOptions()
                .match(pattern)
                .count(count)
                .build();

        RedisConnection redisConnection = stringRedisTemplate.getConnectionFactory().getConnection();
        Cursor<byte[]> cursor = redisConnection.keyCommands().scan(options);

        try(cursor) {
            while (cursor.hasNext()) {
                keys.add(new String(cursor.next()));
            }
        } catch (Exception e) {
            throw new RedisException(e.getMessage());
        }

        return keys;
    }
}
