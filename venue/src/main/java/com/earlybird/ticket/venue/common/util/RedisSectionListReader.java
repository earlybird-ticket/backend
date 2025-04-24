package com.earlybird.ticket.venue.common.util;

import com.earlybird.ticket.venue.application.dto.response.SectionListQuery;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.Map;

@Component
public class RedisSectionListReader extends AbstractRedisHashReader<SectionListQuery.SectionQuery> {

    public RedisSectionListReader(StringRedisTemplate stringRedisTemplate) {
        super(stringRedisTemplate);
    }

    @Override
    protected SectionListQuery.SectionQuery mapToDto(String key, Map<String, String> map) {
        String section = key.split(":")[3];

        return SectionListQuery.SectionQuery.from(
                section,
                Long.parseLong(map.get("remainingSeat")),
                Integer.parseInt(map.get("floor")),
                map.get("grade"),
                new BigDecimal(map.get("price"))
        );
    }
}
