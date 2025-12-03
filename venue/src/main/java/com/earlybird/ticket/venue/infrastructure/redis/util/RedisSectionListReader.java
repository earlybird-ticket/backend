package com.earlybird.ticket.venue.infrastructure.redis.util;

import com.earlybird.ticket.venue.application.dto.response.SectionListQuery;
import org.springframework.data.redis.connection.StringRedisConnection;
import org.springframework.data.redis.core.RedisCallback;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

@Component
public class RedisSectionListReader extends AbstractRedisHashReader<SectionListQuery.SectionQuery> {

    public RedisSectionListReader(StringRedisTemplate stringRedisTemplate) {
        super(stringRedisTemplate);
    }

    @Override
    protected List<Object> fetchRawData(List<String> keys) {
        List<Object> results = getStringRedisTemplate().executePipelined((RedisCallback<Object>) connection -> {
            StringRedisConnection stringConn = (StringRedisConnection) connection;

            for (String key : keys) {
                stringConn.hGetAll(key);
            }
            return null;
        });
        return results;
    }

    @Override
    protected SectionListQuery.SectionQuery mapToDto(String key, Object data) {
        String section = key.split(":")[3];
        Map<String, String> map = (Map<String, String>) data;

        return SectionListQuery.SectionQuery.from(
                section,
                Long.parseLong(map.getOrDefault("remainingSeat", "0")),
                Integer.parseInt(map.getOrDefault("floor", "1")),
                map.get("grade"),
                new BigDecimal(map.getOrDefault("price", "0"))
        );
    }
}
