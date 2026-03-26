package com.earlybird.ticket.venue.infrastructure.redis;


import com.earlybird.ticket.common.util.CommonUtil;
import com.earlybird.ticket.venue.domain.dto.WarmupSeatResult;
import com.earlybird.ticket.venue.domain.entity.constant.Grade;
import com.earlybird.ticket.venue.domain.entity.constant.Section;
import com.earlybird.ticket.venue.domain.entity.constant.Status;
import com.earlybird.ticket.venue.infrastructure.redis.util.RedisKeyFactory;
import com.earlybird.ticket.venue.infrastructure.redis.util.RedisKeyScanner;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.BDDMockito;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.redis.connection.StringRedisConnection;
import org.springframework.data.redis.core.RedisCallback;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.lang.NonNull;

@ExtendWith(MockitoExtension.class)
class SeatWarmupRedisCacheLoaderTest {

    private SeatWarmupRedisCacheLoader seatWarmupRedisCacheLoader;

    @Mock
    private StringRedisTemplate stringRedisTemplate;

    private RedisKeyFactory redisKeyFactory;

    @Mock
    private RedisKeyScanner redisKeyScanner;

    @Mock
    private StringRedisConnection stringRedisConnection;

    @BeforeEach
    void setUp() {
        redisKeyFactory = new RedisKeyFactory(redisKeyScanner, stringRedisTemplate);
        seatWarmupRedisCacheLoader
            = new SeatWarmupRedisCacheLoader(stringRedisTemplate, redisKeyFactory);
    }


    @Test
    void 좌석_상세_해시를_적재한다() {
        // given
        UUID concertId = UUID.randomUUID();
        UUID concertSequenceId = UUID.randomUUID();
        List<WarmupSeatResult> seatResults = warmupSeatResults(concertId, concertSequenceId);
        Map<UUID, LocalDateTime> ticketDeadline = deadline(concertSequenceId, 10, 0);
        Map<UUID, LocalDateTime> vipTicketDeadline = deadline(concertSequenceId, 9, 50);

        stubPipelinedExecution();

        // when
        seatWarmupRedisCacheLoader.load(seatResults, ticketDeadline, vipTicketDeadline);

        // then
        for (WarmupSeatResult seat : seatResults) {
            String seatInstanceKey = redisKeyFactory.generateSeatInstanceKey(
                seat.concertSequenceId(), seat.seatInstanceId()
            );

            BDDMockito.then(stringRedisConnection).should().hSet(
                seatInstanceKey,
                "concertId",
                seat.concertId().toString()
            );

            BDDMockito.then(stringRedisConnection).should().hSet(
                seatInstanceKey,
                "section",
                seat.section().getValue()
            );

            BDDMockito.then(stringRedisConnection).should().hSet(
                seatInstanceKey,
                "status",
                seat.status().getValue()
            );

            BDDMockito.then(stringRedisConnection).should().hSet(
                seatInstanceKey,
                "expiredAt",
                CommonUtil.LocalDateTimetoString(ticketDeadline.get(seat.concertSequenceId()))
            );

            BDDMockito.then(stringRedisConnection).should().hSet(
                seatInstanceKey,
                "vipExpiredAt",
                CommonUtil.LocalDateTimetoString(vipTicketDeadline.get(seat.concertSequenceId()))
            );
        }
    }

    @Test
    void 섹션별_요약_해시를_적재한다() {
        // given
        UUID concertId = UUID.randomUUID();
        UUID concertSequenceId = UUID.randomUUID();
        List<WarmupSeatResult> seatResults = warmupSeatResults(concertId, concertSequenceId);
        Map<UUID, LocalDateTime> ticketDeadline = deadline(concertSequenceId, 10, 0);
        Map<UUID, LocalDateTime> vipTicketDeadline = deadline(concertSequenceId, 9, 50);

        stubPipelinedExecution();

        Map<Section, BigDecimal> expectedPrice = Map.of(
            Section.A, BigDecimal.valueOf(20_000),
            Section.B, BigDecimal.valueOf(10_000)
        );

        Map<Section, Grade> expectedGrade = Map.of(
            Section.A, Grade.S,
            Section.B, Grade.R
        );

        Map<Section, String> expectedFloor = Map.of(
            Section.A, "1",
            Section.B, "1"
        );

        // when
        seatWarmupRedisCacheLoader.load(seatResults, ticketDeadline, vipTicketDeadline);

        Map<Section, Long> counts = seatResults.stream()
            .collect(
                Collectors.groupingBy(WarmupSeatResult::section, Collectors.counting()));

        // then
        for (Map.Entry<Section, Long> entry : counts.entrySet()) {
            String sectionListKey = redisKeyFactory.generateSectionListKey(
                concertId, concertSequenceId, entry.getKey().getValue()
            );

            int count = entry.getValue().intValue();
            BDDMockito.then(stringRedisConnection).should(BDDMockito.times(count)).hIncrBy(
                BDDMockito.eq(sectionListKey),
                BDDMockito.eq("remainingSeat"),
                BDDMockito.eq(1L)
            );

            BDDMockito.then(stringRedisConnection).should(BDDMockito.times(count)).hSet(
                BDDMockito.eq(sectionListKey),
                BDDMockito.eq("floor"),
                BDDMockito.eq(expectedFloor.get(entry.getKey()))
            );

            BDDMockito.then(stringRedisConnection).should(BDDMockito.times(count)).hSet(
                BDDMockito.eq(sectionListKey),
                BDDMockito.eq("grade"),
                BDDMockito.eq(expectedGrade.get(entry.getKey()).getValue())
            );

            BDDMockito.then(stringRedisConnection).should(BDDMockito.times(count)).hSet(
                BDDMockito.eq(sectionListKey),
                BDDMockito.eq("price"),
                BDDMockito.eq(expectedPrice.get(entry.getKey()).toString())
            );

        }
    }

    @Test
    void 섹션별_좌석_인덱스_zset을_적재한다() {
        // given
        UUID concertId = UUID.randomUUID();
        UUID concertSequenceId = UUID.randomUUID();
        List<WarmupSeatResult> seatResults = warmupSeatResults(concertId, concertSequenceId);
        Map<UUID, LocalDateTime> ticketDeadline = deadline(concertSequenceId, 10, 0);
        Map<UUID, LocalDateTime> vipTicketDeadline = deadline(concertSequenceId, 9, 50);

        stubPipelinedExecution();

        // when
        seatWarmupRedisCacheLoader.load(seatResults, ticketDeadline, vipTicketDeadline);

        // then
        for (WarmupSeatResult seat : seatResults) {
            String seatIndexKey = redisKeyFactory.generateSeatIndexKey(
                concertSequenceId, seat.section().getValue()
            );

            BDDMockito.then(stringRedisConnection).should().zAdd(
                seatIndexKey,
                seat.row() * 10000 + seat.col(),
                String.valueOf(seat.seatInstanceId())
            );
        }
    }

    @NonNull
    private static Map<UUID, LocalDateTime> deadline(UUID concertSequenceId, int hour, int minute) {
        return Map.of(
            concertSequenceId, LocalDateTime.of(2026, 3, 26, hour, minute)
        );
    }

    private void stubPipelinedExecution() {
        BDDMockito.given(stringRedisTemplate.executePipelined(BDDMockito.any(RedisCallback.class)))
            .willAnswer(invocation -> {
                RedisCallback<?> callback = invocation.getArgument(0);
                callback.doInRedis(stringRedisConnection);
                return List.of();
            });
    }

    private List<WarmupSeatResult> warmupSeatResults(
        UUID concertId,
        UUID concertSequenceId
    ) {
        return List.of(
            new WarmupSeatResult(
                UUID.fromString("50000000-0000-0000-0000-000000000001"),
                UUID.fromString("40000000-0000-0000-0000-000000000001"),
                concertId,
                concertSequenceId,
                Section.A,
                1,
                1,
                1,
                Grade.S,
                BigDecimal.valueOf(20_000),
                Status.FREE
            ),
            new WarmupSeatResult(
                UUID.fromString("50000000-0000-0000-0000-000000000002"),
                UUID.fromString("40000000-0000-0000-0000-000000000002"),
                concertId,
                concertSequenceId,
                Section.A,
                1,
                2,
                1,
                Grade.S,
                BigDecimal.valueOf(20_000),
                Status.FREE
            ),
            new WarmupSeatResult(
                UUID.fromString("50000000-0000-0000-0000-000000000003"),
                UUID.fromString("40000000-0000-0000-0000-000000000003"),
                concertId,
                concertSequenceId,
                Section.B,
                1,
                1,
                1,
                Grade.R,
                BigDecimal.valueOf(10_000),
                Status.FREE
            )
            , new WarmupSeatResult(
                UUID.fromString("50000000-0000-0000-0000-000000000004"),
                UUID.fromString("40000000-0000-0000-0000-000000000004"),
                concertId,
                concertSequenceId,
                Section.B,
                2,
                1,
                1,
                Grade.R,
                BigDecimal.valueOf(10_000),
                Status.FREE
            )
        );
    }

}
