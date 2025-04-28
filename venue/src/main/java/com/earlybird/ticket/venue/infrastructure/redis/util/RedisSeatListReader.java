package com.earlybird.ticket.venue.infrastructure.redis.util;

import com.earlybird.ticket.venue.application.dto.response.SeatListQuery;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.Map;
import java.util.UUID;

@Component
public class RedisSeatListReader extends AbstractRedisHashReader<SeatListQuery.SeatQuery>{

    public RedisSeatListReader(StringRedisTemplate stringRedisTemplate) {
        super(stringRedisTemplate);
    }

    @Override
    protected SeatListQuery.SeatQuery mapToDto(String key, Map<String, String> map) {
        String seatInstanceId = key.split(":")[2];

        return SeatListQuery.SeatQuery.from(
                UUID.fromString(seatInstanceId),
                Integer.parseInt(map.get("row")),
                Integer.parseInt(map.get("col")),
                map.get("status"),
                new BigDecimal(map.get("price"))
        );
    }
}
