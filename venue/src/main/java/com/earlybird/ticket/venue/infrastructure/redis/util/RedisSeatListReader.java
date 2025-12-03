package com.earlybird.ticket.venue.infrastructure.redis.util;

import com.earlybird.ticket.venue.application.dto.response.SeatListQuery;
import com.earlybird.ticket.venue.common.dto.RedisReadResult;
import org.springframework.data.redis.connection.StringRedisConnection;
import org.springframework.data.redis.core.RedisCallback;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.*;

@Component
public class RedisSeatListReader extends AbstractRedisHashReader<SeatListQuery.SeatQuery> {

    public RedisSeatListReader(StringRedisTemplate stringRedisTemplate) {
        super(stringRedisTemplate);
    }

    private static final String[] SECTION_FIELDS = new String[]{"floor", "grade", "concertId"};
    private static final String[] INSTANCE_FIELDS = new String[]{"row", "col", "status", "price"};

    private static final int IDX_ROW = 0;
    private static final int IDX_COL = 1;
    private static final int IDX_STATUS = 2;
    private static final int IDX_PRICE = 3;

    @Override
    protected List<Object> executePipeline(List<String> keys) {

        return getStringRedisTemplate().executePipelined((RedisCallback<Object>) connection -> {
            StringRedisConnection stringConn = (StringRedisConnection) connection;
            // 섹션 정보
            stringConn.hMGet(keys.get(0), SECTION_FIELDS);
            // 좌석 인스턴스 별 정보
            for (String key : keys) {
                stringConn.hMGet(key, INSTANCE_FIELDS);
            }
            return null;
        });
    }

    // SeatReader 전용 메서드 (헤더와 바디)
    public RedisReadResult<SeatListQuery.SeatQuery> readWithRaw(List<String> keys) {

        List<Object> results = executePipeline(keys);

        // 1. 헤더(섹션 정보) 파싱 - 0번 인덱스
        List<String> headerData = (List<String>) results.get(0);
        Map<String, String> sectionMap = new HashMap<>();
        if(headerData != null){
            for (int i = 0; i < SECTION_FIELDS.length; i++) {
                sectionMap.put(SECTION_FIELDS[i], headerData.get(i));
            }
        }

        //2. 바디(좌석 정보) 파싱 - 1번 인덱스부터 끝까지
        List<SeatListQuery.SeatQuery> dtoList = new ArrayList<>();
        for (int i = 1; i < results.size(); i++) {
            String key = keys.get(i - 1);
            dtoList.add(mapToDto(key, results.get(i)));
        }
        return new RedisReadResult<>(dtoList, sectionMap);
    }

    @Override
    protected SeatListQuery.SeatQuery mapToDto(String key, Object data) {
        if (data == null){
            return null;
        }

        List<String> result = (List<String>) data;
        String seatInstanceId = key.substring(key.lastIndexOf(':') + 1);

        return SeatListQuery.SeatQuery.from(
                UUID.fromString(seatInstanceId),
                parseIntOrZero(result.get(IDX_ROW)),
                parseIntOrZero(result.get(IDX_COL)),
                result.get(IDX_STATUS),
                new BigDecimal(result.get(IDX_PRICE) != null ? result.get(IDX_PRICE) : "0")
        );
    }

    private int parseIntOrZero(String val) {
        return val != null ? Integer.parseInt(val) : 0;
    }
}
