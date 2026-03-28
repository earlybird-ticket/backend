package com.earlybird.ticket.venue.infrastructure.redis;

import com.earlybird.ticket.common.util.CommonUtil;
import com.earlybird.ticket.venue.application.SeatWarmupCacheLoader;
import com.earlybird.ticket.venue.domain.dto.WarmupSeatResult;
import com.earlybird.ticket.venue.infrastructure.redis.util.RedisKeyFactory;
import java.time.LocalDateTime;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.connection.StringRedisConnection;
import org.springframework.data.redis.core.RedisCallback;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class SeatWarmupRedisCacheLoader implements SeatWarmupCacheLoader {

    private final StringRedisTemplate stringRedisTemplate;
    private final RedisKeyFactory redisKeyFactory;

    /**
     반드시 동일 공연 회차 및 섹션이며, 행/열 순으로 정렬된 입력이 들어와야 한다.
     */
    @Override
    public void load(
        List<WarmupSeatResult> seats,
        LocalDateTime ticketDeadline,
        LocalDateTime vipTicketDeadline
    ) {
        if (seats == null || seats.isEmpty()) {
            throw new IllegalArgumentException("좌석 warmup 캐시 적재 입력은 비어 있을 수 없습니다.");
        }

        stringRedisTemplate.executePipelined(
            (RedisCallback<Object>) connection -> {
                StringRedisConnection stringConn = (StringRedisConnection) connection;

                for (int layoutIndex = 0; layoutIndex < seats.size(); layoutIndex++) {
                    WarmupSeatResult seatInfo = seats.get(layoutIndex);

                    makeSeatInstanceOnRedis(seatInfo, stringConn, ticketDeadline, vipTicketDeadline);
                    makeSectionListOnRedis(seatInfo, stringConn);
                    makeSeatIndexOnRedis(seatInfo, stringConn, layoutIndex);
                }
                return null;
            }
        );

    }

    private void makeSeatIndexOnRedis(
        WarmupSeatResult seatInfo, StringRedisConnection stringConn, int layoutIndex
    ) {
        String seatIndexKey = redisKeyFactory.generateSeatIndexKey(
            seatInfo.concertSequenceId(), seatInfo.section().getValue());

        stringConn.sAdd(seatIndexKey, String.valueOf(layoutIndex));
    }

    private void makeSectionListOnRedis(WarmupSeatResult seatInfo,
        StringRedisConnection stringConn) {
        String sectionKey = redisKeyFactory.generateSectionListKey(
            seatInfo.concertId(),
            seatInfo.concertSequenceId(),
            seatInfo.section().getValue()
        );

        stringConn.hIncrBy(sectionKey, "remainingSeat", 1);
        stringConn.hSet(sectionKey, "floor", seatInfo.floor().toString());
        stringConn.hSet(sectionKey, "grade", seatInfo.grade().getValue());
        stringConn.hSet(sectionKey, "price", seatInfo.price().toString());
    }

    private void makeSeatInstanceOnRedis(
        WarmupSeatResult seatInfo,
        StringRedisConnection stringConn,
        LocalDateTime ticketDeadline,
        LocalDateTime vipTicketDeadline
    ) {
        String seatInstanceKey = redisKeyFactory.generateSeatInstanceKey(
            seatInfo.concertSequenceId(), seatInfo.seatInstanceId());

        stringConn.hSet(seatInstanceKey, "status", seatInfo.status().getValue());
        stringConn.hSet(seatInstanceKey, "userId", "");
        stringConn.hSet(seatInstanceKey, "reservationId", "");
        stringConn.hSet(seatInstanceKey, "concertId", seatInfo.concertId().toString());
        stringConn.hSet(seatInstanceKey, "col", seatInfo.col().toString());
        stringConn.hSet(seatInstanceKey, "row", seatInfo.row().toString());
        stringConn.hSet(seatInstanceKey, "section", seatInfo.section().getValue());
        stringConn.hSet(seatInstanceKey, "floor", seatInfo.floor().toString());
        stringConn.hSet(seatInstanceKey, "grade", seatInfo.grade().getValue());
        stringConn.hSet(seatInstanceKey, "price", seatInfo.price().toString());
        stringConn.hSet(
            seatInstanceKey, "expiredAt", CommonUtil.LocalDateTimetoString(ticketDeadline)
        );
        stringConn.hSet(
            seatInstanceKey, "vipExpiredAt", CommonUtil.LocalDateTimetoString(vipTicketDeadline)
        );
        stringConn.hSet(seatInstanceKey, "updatedAt", "");

    }
}