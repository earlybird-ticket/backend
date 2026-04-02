package com.earlybird.ticket.venue.application.service;

import com.earlybird.ticket.common.entity.EventPayload;
import com.earlybird.ticket.common.entity.PassportDto;
import com.earlybird.ticket.common.util.CommonUtil;
import com.earlybird.ticket.common.util.DataUtil;
import com.earlybird.ticket.common.util.PassportUtil;
import com.earlybird.ticket.venue.application.dto.request.PrepareSeatIndexCommand;
import com.earlybird.ticket.venue.application.dto.request.ProcessSeatCheckCommand;
import com.earlybird.ticket.venue.application.dto.request.SeatPreemptCommand;
import com.earlybird.ticket.venue.application.dto.request.WarmupSeatsCommand;
import com.earlybird.ticket.venue.application.dto.request.WarmupSeatsCommand.ConcertDeadLine;
import com.earlybird.ticket.venue.application.dto.response.ProcessSeatCheckQuery;
import com.earlybird.ticket.venue.application.dto.response.SeatListQueryV2;
import com.earlybird.ticket.venue.application.dto.response.SectionListQuery;
import com.earlybird.ticket.venue.application.event.dto.response.ReservationCreateEvent;
import com.earlybird.ticket.venue.common.event.EventType;
import com.earlybird.ticket.venue.common.exception.RedisException;
import com.earlybird.ticket.venue.common.exception.SeatUnavailableException;
import com.earlybird.ticket.venue.common.util.EventConverter;
import com.earlybird.ticket.venue.infrastructure.redis.util.RedisKeyFactory;
import com.earlybird.ticket.venue.infrastructure.redis.util.RedisSeatListReader;
import com.earlybird.ticket.venue.infrastructure.redis.util.RedisSectionListReader;
import com.earlybird.ticket.venue.domain.entity.Event;
import com.earlybird.ticket.venue.domain.entity.Outbox;
import com.earlybird.ticket.venue.domain.entity.Seat;
import com.earlybird.ticket.venue.domain.entity.SeatInstance;
import com.earlybird.ticket.venue.domain.repository.OutboxRepository;
import com.earlybird.ticket.venue.domain.repository.SeatRepository;
import com.earlybird.ticket.venue.infrastructure.redis.config.RedisConfig;
import io.micrometer.core.instrument.MeterRegistry;
import java.util.function.Function;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RedissonClient;
import org.springframework.data.redis.connection.StringRedisConnection;
import org.springframework.data.redis.core.RedisCallback;
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
    public SeatListQueryV2 findSeatList(UUID concertSequenceId, String section) {
        String seatIndexKey = redisKeyFactory.generateSeatIndexKey(
            concertSequenceId,
            section
        );
        List<String> seatQueries = getSeatQueries(seatIndexKey, section);

        if (seatQueries.isEmpty()) {
            return SeatListQueryV2.from(
                null,
                concertSequenceId,
                section,
                null,
                null,
                Collections.emptyList()
            );
        }

        String seatQuery = seatQueries.get(0);

        String seatInstanceKey = redisKeyFactory.generateSeatInstanceKey(concertSequenceId, extractSeatInstanceId(seatQuery));
        List<Object> objects = stringRedisTemplate.opsForHash()
                .multiGet(seatInstanceKey, List.of("concertId", "grade", "floor"));

        return SeatListQueryV2.from(
            UUID.fromString((String)objects.get(0)),
            concertSequenceId,
            section,
            (String)objects.get(1),
            Integer.parseInt((String)objects.get(2)),
            seatQueries
        );

    }

    private UUID extractSeatInstanceId(String seatQuery) {
        String keyPattern = "\"seat_instance_id\":\"";
        int startIdx = seatQuery.indexOf(keyPattern) + keyPattern.length();
        int endIdx = seatQuery.indexOf("\"", startIdx);
        String uuidString =  seatQuery.substring(startIdx, endIdx);
        return UUID.fromString(uuidString);
    }

    private List<String> getSeatQueries(String seatIndexKey, String section) {
        long startRead = System.currentTimeMillis();
        Set<String> range = stringRedisTemplate.opsForZSet()
                .range(seatIndexKey, 0, -1);
        meterRegistry.timer("sectionListSelectTimeMillis", ":" , section)
                .record(System.currentTimeMillis() - startRead, TimeUnit.MILLISECONDS);
        return Optional.ofNullable(range)
                .orElse(Collections.emptySet())
                .stream().toList();
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

    // TODO : 추후 배치로 고도화 고려 vs Kafka Consumer
    public void warmUpSeatInstance(WarmupSeatsCommand warmupSeatsCommand) {
        Map<UUID, ConcertDeadLine> ticketDeadline = warmupSeatsCommand.concertDeadLines().stream()
            .collect(Collectors.toMap(
                ConcertDeadLine::concertSequenceId,
                Function.identity(),
                (a, b) -> a
            ));

        // 테스트용으로 동일 값 사용
        List<UUID> concertSequenceIdList = new ArrayList<>(ticketDeadline.keySet());

        List<Seat> seats = seatRepository.findSeatListWithSeatInstanceInConcertSequenceIdList(
            concertSequenceIdList
        );

        List<Object> results = stringRedisTemplate.executePipelined(
            (RedisCallback<Object>) connection -> {
                StringRedisConnection stringConn = (StringRedisConnection) connection;

                for (Seat seat : seats) {
                    for (SeatInstance seatInstance : seat.getSeatInstances()) {
                        PrepareSeatIndexCommand seatIndexCommand = PrepareSeatIndexCommand.fromSeatInstance(
                            seatInstance
			 );
                        String value = DataUtil.serialize(seatIndexCommand);

                        if (value == null) {
                            continue;
                        }

                        makeSeatInstanceOnRedis(seat, seatInstance, value, stringConn,
                            ticketDeadline);
                        makeSectionListOnRedis(seat, seatInstance, stringConn);
                        makeSeatIndexOnRedis(seat, seatInstance, value, stringConn);
                    }
                }

                return null;
            });

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

    private void makeSeatIndexOnRedis(Seat seat, SeatInstance seatInstance, String value,
        StringRedisConnection stringConn) {
        String seatIndexKey = redisKeyFactory.generateSeatIndexKey(
            seatInstance.getConcertSequenceId(), seat.getSection().getValue());

        stringConn.zAdd(seatIndexKey, seat.getRow() * 10000 + seat.getCol(), value);
    }

    private void makeSectionListOnRedis(Seat seat, SeatInstance seatInstance,
        StringRedisConnection stringConn) {
        String sectionKey = redisKeyFactory.generateSectionListKey(
            seatInstance.getConcertId(),
            seatInstance.getConcertSequenceId(),
            seat.getSection().getValue()
        );

        stringConn.hIncrBy(sectionKey, "remainingSeat", 1);
        stringConn.hSet(sectionKey, "floor", seat.getFloor().toString());
        stringConn.hSet(sectionKey, "grade", seatInstance.getGrade().getValue());
        stringConn.hSet(sectionKey, "price", seatInstance.getPrice().toString());
    }

    private void makeSeatInstanceOnRedis(
        Seat seat,
        SeatInstance seatInstance,
        String value,
        StringRedisConnection stringConn,
        Map<UUID, ConcertDeadLine> deadLineMap
    ) {
        String seatInstanceKey = redisKeyFactory.generateSeatInstanceKey(
            seatInstance.getConcertSequenceId(), seatInstance.getId());

        stringConn.hSet(seatInstanceKey, "status", seatInstance.getStatus().getValue());
        stringConn.hSet(seatInstanceKey, "userId", "");
        stringConn.hSet(seatInstanceKey, "reservationId", "");
        stringConn.hSet(seatInstanceKey, "concertId", seatInstance.getConcertId().toString());
        stringConn.hSet(seatInstanceKey, "col", seat.getCol().toString());
        stringConn.hSet(seatInstanceKey, "row", seat.getRow().toString());
        stringConn.hSet(seatInstanceKey, "section", seat.getSection().getValue());
        stringConn.hSet(seatInstanceKey, "floor", seat.getFloor().toString());
        stringConn.hSet(seatInstanceKey, "grade", seatInstance.getGrade().getValue());
        stringConn.hSet(seatInstanceKey, "price", seatInstance.getPrice().toString());
        stringConn.hSet(seatInstanceKey, "expiredAt", CommonUtil.LocalDateTimetoString(
            deadLineMap.get(seatInstance.getConcertSequenceId()).ticketExpiredAt()));
        stringConn.hSet(seatInstanceKey, "vipExpiredAt", CommonUtil.LocalDateTimetoString(
            deadLineMap.get(seatInstance.getConcertSequenceId()).vipTicketExpiredAt()));
        stringConn.hSet(seatInstanceKey, "updatedAt", "");
        stringConn.hSet(seatInstanceKey, "seatIndexValue", value);
    }
}

