package com.earlybird.ticket.venue.infrastructure.redis;

import com.earlybird.ticket.common.util.CommonUtil;
import com.earlybird.ticket.venue.application.SeatWarmupCacheLoader;
import com.earlybird.ticket.venue.domain.dto.WarmupSeatResult;
import com.earlybird.ticket.venue.infrastructure.redis.util.RedisKeyFactory;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.UUID;
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

    @Override
    public void load(
        List<WarmupSeatResult> seats,
        Map<UUID, LocalDateTime> ticketDeadline,
        Map<UUID, LocalDateTime> vipTicketDeadline
    ) {
        stringRedisTemplate.executePipelined(
            (RedisCallback<Object>) connection -> {
                StringRedisConnection stringConn = (StringRedisConnection) connection;

                for (WarmupSeatResult seatInfo : seats) {
                    makeSeatInstanceOnRedis(seatInfo, stringConn, ticketDeadline,
                        vipTicketDeadline);
                    makeSectionListOnRedis(seatInfo, stringConn);
                    makeSeatIndexOnRedis(seatInfo, stringConn);
                }
                return null;
            }
        );

    }

    private void makeSeatIndexOnRedis(
        WarmupSeatResult seatInfo, StringRedisConnection stringConn) {

        String seatIndexKey = redisKeyFactory.generateSeatIndexKey(
            seatInfo.concertSequenceId(), seatInfo.section().getValue());

        stringConn.zAdd(seatIndexKey, seatInfo.row() * 10000 + seatInfo.col(),
            String.valueOf(seatInfo.seatInstanceId()));
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
        Map<UUID, LocalDateTime> ticketDeadline,
        Map<UUID, LocalDateTime> vipTicketDeadline
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
        stringConn.hSet(seatInstanceKey, "expiredAt", CommonUtil.LocalDateTimetoString(
            ticketDeadline.get(seatInfo.concertSequenceId())));
        stringConn.hSet(seatInstanceKey, "vipExpiredAt", CommonUtil.LocalDateTimetoString(
            vipTicketDeadline.get(seatInfo.concertSequenceId())));
        stringConn.hSet(seatInstanceKey, "updatedAt", "");

    }
}