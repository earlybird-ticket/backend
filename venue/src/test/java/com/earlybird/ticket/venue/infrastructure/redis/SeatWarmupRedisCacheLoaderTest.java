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
import java.util.LinkedHashMap;
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
import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.RedisCallback;
import org.springframework.data.redis.core.StringRedisTemplate;

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

    @Mock
    private HashOperations<String, Object, Object> hashOperations;

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
        LinkedHashMap<Section, List<WarmupSeatResult>> warmupSeatResultsBySection =
            getWarmupSeatResultsBySection(concertId, concertSequenceId);
        LocalDateTime ticketDeadline = deadline(10, 0);
        LocalDateTime vipTicketDeadline = deadline(9, 50);

        stubPipelinedExecution();
        BDDMockito.given(stringRedisTemplate.opsForHash()).willReturn(hashOperations);

        // when

        for (Section section : warmupSeatResultsBySection.keySet()) {
            List<WarmupSeatResult> seatResults = warmupSeatResultsBySection.get(section);
            seatWarmupRedisCacheLoader.load(seatResults, ticketDeadline, vipTicketDeadline);

            // then
            for (WarmupSeatResult seat : seatResults) {
                String seatInstanceKey = redisKeyFactory.generateSeatInstanceKey(
                    seat.concertSequenceId(), seat.seatInstanceId()
                );

                BDDMockito.then(stringRedisConnection).should(BDDMockito.times(1)).hSet(
                    seatInstanceKey,
                    "concertId",
                    seat.concertId().toString()
                );

                BDDMockito.then(stringRedisConnection).should(BDDMockito.times(1)).hSet(
                    seatInstanceKey,
                    "section",
                    seat.section().getValue()
                );

                BDDMockito.then(stringRedisConnection).should(BDDMockito.times(1)).hSet(
                    seatInstanceKey,
                    "status",
                    seat.status().getValue()
                );

                BDDMockito.then(stringRedisConnection).should(BDDMockito.times(1)).hSet(
                    seatInstanceKey,
                    "expiredAt",
                    CommonUtil.LocalDateTimetoString(ticketDeadline)
                );

                BDDMockito.then(stringRedisConnection).should(BDDMockito.times(1)).hSet(
                    seatInstanceKey,
                    "vipExpiredAt",
                    CommonUtil.LocalDateTimetoString(vipTicketDeadline)
                );
            }
        }
    }

    @Test
    void 섹션별_요약_해시를_적재한다() {
        // given
        UUID concertId = UUID.randomUUID();
        UUID concertSequenceId = UUID.randomUUID();
        LinkedHashMap<Section, List<WarmupSeatResult>> warmupSeatResultsBySection =
            getWarmupSeatResultsBySection(concertId, concertSequenceId);

        LocalDateTime ticketDeadline = deadline(10, 0);
        LocalDateTime vipTicketDeadline = deadline(9, 50);

        stubPipelinedExecution();
        BDDMockito.given(stringRedisTemplate.opsForHash()).willReturn(hashOperations);

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
        for (Section section : warmupSeatResultsBySection.keySet()) {
            List<WarmupSeatResult> seatResults = warmupSeatResultsBySection.get(section);
            seatWarmupRedisCacheLoader.load(seatResults, ticketDeadline, vipTicketDeadline);

            int count = seatResults.size();

            String sectionListKey = redisKeyFactory.generateSectionListKey(
                concertId, concertSequenceId, section.getValue()
            );

            // then
            BDDMockito.then(hashOperations).should(BDDMockito.times(1)).putAll(
                BDDMockito.eq(sectionListKey),
                BDDMockito.argThat((Map<String, String> map) ->
                    expectedGrade.get(section).getValue().equals(map.get("grade"))
                        && String.valueOf(count).equals(map.get("remainingSeat"))
                        && expectedPrice.get(section).toString().equals(map.get("price"))
                        && expectedFloor.get(section).equals(map.get("floor"))
                ));
        }
        BDDMockito.then(hashOperations).shouldHaveNoMoreInteractions();
    }

    @Test
    void 섹션별_좌석_인덱스_set을_적재한다() {
        // given
        UUID concertId = UUID.randomUUID();
        UUID concertSequenceId = UUID.randomUUID();
        LinkedHashMap<Section, List<WarmupSeatResult>> warmupSeatResultsBySection =
            getWarmupSeatResultsBySection(concertId, concertSequenceId);
        LocalDateTime ticketDeadline = deadline(10, 0);
        LocalDateTime vipTicketDeadline = deadline(9, 50);

        stubPipelinedExecution();
        BDDMockito.given(stringRedisTemplate.opsForHash()).willReturn(hashOperations);

        // when & then
        for (Section section : warmupSeatResultsBySection.keySet()) {
            List<WarmupSeatResult> seatResults = warmupSeatResultsBySection.get(section);
            seatWarmupRedisCacheLoader.load(seatResults, ticketDeadline, vipTicketDeadline);

            String seatIndexKey = redisKeyFactory.generateSeatIndexKey(
                concertSequenceId, section.getValue()
            );

            for (int layoutIndex = 0; layoutIndex < seatResults.size(); layoutIndex++) {
                BDDMockito.then(stringRedisConnection).should(BDDMockito.times(1))
                    .sAdd(seatIndexKey, String.valueOf(layoutIndex));
            }
        }
    }

    @Test
    void 같은_섹션_warmup을_재실행해도_remainingSeat는_누적되지_않는다() {
        // given
        UUID concertId = UUID.randomUUID();
        UUID concertSequenceId = UUID.randomUUID();
        LinkedHashMap<Section, List<WarmupSeatResult>> warmupSeatResultsBySection =
            getWarmupSeatResultsBySection(concertId, concertSequenceId);

        List<WarmupSeatResult> seatResults = warmupSeatResultsBySection.get(Section.A);
        WarmupSeatResult firstSeat = seatResults.get(0);
        Integer floor = firstSeat.floor();
        BigDecimal price = firstSeat.price();
        Grade grade = firstSeat.grade();

        LocalDateTime ticketDeadline = deadline(10, 0);
        LocalDateTime vipTicketDeadline = deadline(9, 50);

        stubPipelinedExecution();
        BDDMockito.given(stringRedisTemplate.opsForHash()).willReturn(hashOperations);

        String sectionListKey = redisKeyFactory.generateSectionListKey(
            concertId, concertSequenceId, Section.A.getValue()
        );

        // when
        seatWarmupRedisCacheLoader.load(seatResults, ticketDeadline, vipTicketDeadline);
        seatWarmupRedisCacheLoader.load(seatResults, ticketDeadline, vipTicketDeadline);

        // then
        BDDMockito.then(hashOperations).should(BDDMockito.times(2))
            .putAll(
                BDDMockito.eq(sectionListKey),
                BDDMockito.argThat((Map<String, String> actual) ->
                    grade.getValue().equals(actual.get("grade"))
                        && floor.toString().equals(actual.get("floor"))
                        && price.toString().equals(actual.get("price"))
                        && String.valueOf(seatResults.size()).equals(actual.get("remainingSeat"))
                )
            );
    }

    private LocalDateTime deadline(int hour, int minute) {
        return LocalDateTime.of(2026, 3, 26, hour, minute);
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

    private LinkedHashMap<Section, List<WarmupSeatResult>> getWarmupSeatResultsBySection(
        UUID concertId,
        UUID concertSequenceId
    ) {
        return warmupSeatResults(concertId, concertSequenceId).stream()
            .collect(Collectors.groupingBy(
                    WarmupSeatResult::section,
                    LinkedHashMap::new,
                    Collectors.toList()
                )
            );
    }

}
