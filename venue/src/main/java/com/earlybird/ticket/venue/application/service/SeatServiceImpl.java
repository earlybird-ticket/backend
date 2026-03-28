package com.earlybird.ticket.venue.application.service;

import com.earlybird.ticket.common.entity.EventPayload;
import com.earlybird.ticket.common.entity.PassportDto;
import com.earlybird.ticket.common.util.CommonUtil;
import com.earlybird.ticket.common.util.PassportUtil;
import com.earlybird.ticket.venue.application.dto.request.ProcessSeatCheckCommand;
import com.earlybird.ticket.venue.application.dto.request.SeatPreemptCommand;
import com.earlybird.ticket.venue.application.dto.response.ProcessSeatCheckQuery;
import com.earlybird.ticket.venue.application.dto.response.SeatListQuery;
import com.earlybird.ticket.venue.application.dto.response.SectionListQuery;
import com.earlybird.ticket.venue.application.event.dto.response.ReservationCreateEvent;
import com.earlybird.ticket.venue.common.dto.RedisReadResult;
import com.earlybird.ticket.venue.common.event.EventType;
import com.earlybird.ticket.venue.common.exception.RedisException;
import com.earlybird.ticket.venue.common.exception.SeatUnavailableException;
import com.earlybird.ticket.venue.common.util.EventConverter;
import com.earlybird.ticket.venue.domain.dto.WarmupSeatResult;
import com.earlybird.ticket.venue.infrastructure.redis.util.RedisKeyFactory;
import com.earlybird.ticket.venue.infrastructure.redis.util.RedisSeatListReader;
import com.earlybird.ticket.venue.infrastructure.redis.util.RedisSectionListReader;
import com.earlybird.ticket.venue.domain.entity.Event;
import com.earlybird.ticket.venue.domain.entity.Outbox;
import com.earlybird.ticket.venue.domain.repository.OutboxRepository;
import com.earlybird.ticket.venue.domain.repository.SeatRepository;
import com.earlybird.ticket.venue.infrastructure.redis.config.RedisConfig;
import io.micrometer.core.instrument.MeterRegistry;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RedissonClient;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.script.RedisScript;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;


@Slf4j
@Service
@RequiredArgsConstructor
public class SeatServiceImpl implements SeatService {

    private final SeatRepository seatRepository;
    private final OutboxRepository outboxRepository;
    private final PassportUtil passportUtil;
    private final EventConverter eventConverter;
    private final RedissonClient redissonClient;
    private final StringRedisTemplate stringRedisTemplate;
    private final RedisConfig redisConfig;
    private final RedisKeyFactory redisKeyFactory;
    private final RedisSeatListReader redisSeatListReader; //하나로 합치기 (예 : 전략패턴)
    private final RedisSectionListReader redisSectionListReader;
    private final MeterRegistry meterRegistry;

    @Override
    public SectionListQuery findSectionList(UUID concertSequenceId) {

        List<String> keys = redisKeyFactory.getAllSectionListKeys(concertSequenceId, 1000L);

        if (keys.isEmpty()) {
            return SectionListQuery.from(
                null,
                concertSequenceId,
                Collections.emptyList());
        }

        List<SectionListQuery.SectionQuery> sectionQueryList = redisSectionListReader.read(keys);

        String concertId = keys.get(0).split(":")[1];
        return SectionListQuery.from(UUID.fromString(concertId), concertSequenceId,
            sectionQueryList);
    }

    @Override
    public SeatListQuery findSeatList(UUID concertSequenceId, String section) {

        List<String> keys = redisKeyFactory.convertSeatIndexKeysToSeatInstanceKeys(
            concertSequenceId,
            section,
            0L,
            -1L
        );

        if (keys.isEmpty()) {
            return SeatListQuery.from(
                null,
                concertSequenceId,
                section,
                null,
                null,
                Collections.emptyList()
            );
        }
        long startRead = System.currentTimeMillis();
        RedisReadResult<SeatListQuery.SeatQuery> readResult = redisSeatListReader.readWithRaw(keys);

        meterRegistry.timer("sectionListSelectTimeMillis", ":" , section)
                .record(System.currentTimeMillis() - startRead, TimeUnit.MILLISECONDS);

        Map<String, String> firstMap = readResult.rawMap();

        UUID concertId = UUID.fromString(firstMap.get("concertId"));
        String grade = firstMap.get("grade");
        Integer floor = Integer.parseInt(firstMap.get("floor"));

        return SeatListQuery.from(
            concertId,
            concertSequenceId,
            section,
            grade,
            floor,
            readResult.results()
        );

    }

    @Override
    public ProcessSeatCheckQuery checkSeat(ProcessSeatCheckCommand processSeatCheckCommand) {
        List<UUID> seatInstanceIdList = processSeatCheckCommand.seatInstanceIdList();

        List<String> seatKeys = seatInstanceIdList.stream()
            .map(seatInstanceId -> redisKeyFactory.generateSeatInstanceKey(
                    processSeatCheckCommand.concertSequenceId(),
                    seatInstanceId
                )
            )
            .toList();

        Object result = stringRedisTemplate.execute(
            redisConfig.seatCheckScript(),
            seatKeys
        );

        if (isAlreadyPreempted(result)) {
            throw new SeatUnavailableException();
        }

        return ProcessSeatCheckQuery.from(seatInstanceIdList, true);
    }

    @Override
    public String preemptSeat(SeatPreemptCommand seatPreemptCommand, String passport) {

        return preemptSeatInternal(passport, seatPreemptCommand, redisConfig.seatPreemptScript());
    }

    @Override
    public String preemptSeatByVIP(SeatPreemptCommand seatPreemptCommand, String passport) {

        return preemptSeatInternal(passport, seatPreemptCommand,
            redisConfig.seatPreemptByVIPScript());
    }

    @Override
    public String preemptWaitingSeatByVIP(SeatPreemptCommand seatPreemptCommand, String passport) {

        return preemptSeatInternal(passport, seatPreemptCommand,
            redisConfig.waitingSeatPreemptByVIPScript());
    }

    @Override
    public Map<UUID, List<WarmupSeatResult>> collectWarmupSeatData(List<UUID> concertSequenceIds) {
        return seatRepository.findSeatInfoByConcertSequenceIdList(concertSequenceIds)
            .stream()
            .collect(
                Collectors.groupingBy(
                    WarmupSeatResult::concertSequenceId,
                    LinkedHashMap::new,
                    Collectors.toList()
                )
            );
    }

    private String preemptSeatInternal(String passport, SeatPreemptCommand seatPreemptCommand,
        RedisScript<Object> redisScript) {
        PassportDto passportDto = passportUtil.getPassportDto(passport);
        Long userId = passportDto.getUserId();
        UUID reservationId = UUID.randomUUID();
        long ttlMs = Duration.ofMinutes(10).toMillis();

        List<UUID> seatInstanceIdList = seatPreemptCommand.seatList().stream()
            .map(SeatPreemptCommand.SeatRequest::seatInstanceId)
            .toList();

        List<String> seatKeys = generateSeatInstanceRedisKeys(seatPreemptCommand,
            seatInstanceIdList);

        Object result = executePreemptLuaScript(
            redisScript,
            seatKeys,
            userId,
            reservationId,
            ttlMs
        );

        if (isAlreadyPreempted(result)) {
            throw new SeatUnavailableException();
        }

        if (!isLuaExecutionSuccess(result)) {
            throw new RedisException();
        }

        saveOutbox(seatInstanceIdList,
            ReservationCreateEvent.toReservationCreateEvent(
                seatPreemptCommand,
                passportDto,
                reservationId
            ),
            EventType.RESERVATION_CREATE);

        return reservationId.toString();
    }


    private Object executePreemptLuaScript(RedisScript<Object> script, List<String> seatKeys,
        Long userId, UUID reservationId, long ttlMs) {
        return stringRedisTemplate.execute(
            script,
            seatKeys,
            userId.toString(),
            reservationId.toString(),
            String.valueOf(ttlMs),
            LocalDateTime.now().toString()
        );
    }

    private List<String> generateSeatInstanceRedisKeys(SeatPreemptCommand seatPreemptCommand,
        List<UUID> seatInstanceIdList) {
        String seatInstancePrefix = redisKeyFactory.generateSeatInstanceKeyWithOutSeatInstanceId(
            seatPreemptCommand.concertSequenceId());

        return seatInstanceIdList.stream()
            .map(seatId -> seatInstancePrefix + seatId)
            .collect(Collectors.toList());
    }

    private boolean isLuaExecutionSuccess(Object result) {
        return "OK".equals(result);
    }

    private boolean isAlreadyPreempted(Object result) {
        return result == null || (result instanceof Number
            && ((Number) result).longValue() == RedisKeyFactory.LUA_FAIL);
    }

    private <T extends EventPayload> void saveOutbox(List<UUID> seatInstanceIdList,
        T eventPayload,
        EventType eventType) {
        Event<T> event = Event.<T>builder()
            .eventType(eventType)
            .payload(eventPayload)
            .timestamp(CommonUtil.LocalDateTimetoString(LocalDateTime.now()))
            .build();

        outboxRepository.save(Outbox.builder()
            .aggregateId(seatInstanceIdList.get(0))
            .aggregateType(Outbox.AggregateType.SEAT_INSTANCE)
            .eventType(eventType)
            .payload(eventConverter.serializeEvent(event))
            .build());
    }
}

