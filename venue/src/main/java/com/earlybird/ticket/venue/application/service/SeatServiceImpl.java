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
import com.earlybird.ticket.venue.common.event.EventType;
import com.earlybird.ticket.venue.common.exception.RedisException;
import com.earlybird.ticket.venue.common.exception.SeatNotFoundException;
import com.earlybird.ticket.venue.common.exception.SeatUnavailableException;
import com.earlybird.ticket.venue.common.util.EventConverter;
import com.earlybird.ticket.venue.common.util.RedisKeyScanner;
import com.earlybird.ticket.venue.domain.entity.Event;
import com.earlybird.ticket.venue.domain.entity.Outbox;
import com.earlybird.ticket.venue.domain.entity.Seat;
import com.earlybird.ticket.venue.domain.entity.SeatInstance;
import com.earlybird.ticket.venue.domain.entity.constant.Section;
import com.earlybird.ticket.venue.domain.entity.constant.Status;
import com.earlybird.ticket.venue.domain.repository.OutboxRepository;
import com.earlybird.ticket.venue.domain.repository.SeatRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RedissonClient;
import org.springframework.data.redis.connection.StringRedisConnection;
import org.springframework.data.redis.core.RedisCallback;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.script.RedisScript;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.*;
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
    private final RedisScript<Object> seatPreemptScript;
    private final RedisKeyScanner redisKeyScanner;

    @Override
    public SectionListQuery findSectionList(UUID concertSequenceId) {

        List<String> keys = redisKeyScanner.scanKeys("SECTION_LIST:*:" + concertSequenceId + ":*", 1000);

        List<Object> results = stringRedisTemplate.executePipelined((RedisCallback<Object>) connection -> {
            StringRedisConnection stringConn = (StringRedisConnection) connection;

            for(String key : keys) {
                stringConn.hGetAll(key);
            }
            return null;
        });

        //DTO 변환
        List<SectionListQuery.SectionQuery> sectionQueryList = new ArrayList<>();

        int index = 0;
        for(String key : keys) {
            Map<String, String> map = (Map<String, String>) results.get(index++);

            String section = key.split(":")[3];

            sectionQueryList.add(SectionListQuery.SectionQuery.from(
                    section,
                    Long.parseLong(map.get("remainingSeat")),
                    Integer.parseInt(map.get("floor")),
                    map.get("grade"),
                    new BigDecimal(map.get("price"))
            ));
        }

        String concertId = keys.get(0).split(":")[1];

        return SectionListQuery.from(UUID.fromString(concertId), concertSequenceId, sectionQueryList);
    }

    @Override
    public SeatListQuery findSeatList(UUID concertSequenceId, String section) {

        List<String> keys = stringRedisTemplate.opsForZSet()
                .range("SEAT_INDEX:"+ concertSequenceId + ":" + section, 0,-1)
                .stream()
                .map(id -> "SEAT_INSTANCE:" + concertSequenceId + ":" + id)
                .collect(Collectors.toList());

        List<Object> results = stringRedisTemplate.executePipelined((RedisCallback<Object>) connect -> {
            StringRedisConnection stringConn = (StringRedisConnection) connect;

            for(String seatInstanceId : keys) {
                stringConn.hGetAll(seatInstanceId);
            }

            return null;
        });

        //DTO 변환
        List<SeatListQuery.SeatQuery> seatQueryList = new ArrayList<>();

        int index = 0;
        for(String key : keys) {
            Map<String, String> map = (Map<String, String>) results.get(index++);

            String seatInstanceId = key.split(":")[2];

            seatQueryList.add(SeatListQuery.SeatQuery.from(
                    UUID.fromString(seatInstanceId),
                    Integer.parseInt(map.get("row")),
                    Integer.parseInt(map.get("col")),
                    map.get("status"),
                    new BigDecimal(map.get("price"))
            ));
        }

        Map<String, String> firstMap = (Map<String, String>) results.get(0);

        UUID concertId = UUID.fromString(firstMap.get("concertId"));
        String grade = firstMap.get("grade");
        Integer floor = Integer.parseInt(firstMap.get("floor"));

        return SeatListQuery.from(
                concertId,
                concertSequenceId,
                section,
                grade,
                floor,
                seatQueryList
        );

    }

    @Override
    public ProcessSeatCheckQuery checkSeat(ProcessSeatCheckCommand processSeatCheckCommand) {
        List<UUID> seatInstanceIdList = processSeatCheckCommand.seatInstanceIdList();
        // 1. seatInstanceId와 일치하는 seat 가져오기
        List<Seat> seatList = seatRepository.findSeatListWithSeatInstanceInSeatInstanceIdList(seatInstanceIdList);

        // 2. seat이 다 존재하는 지 확인
        if(seatList.size() != seatInstanceIdList.size() && seatList.get(0).getSeatInstances().size() != seatInstanceIdList.size()) {
            throw new SeatNotFoundException();
        }

        // 3. SeatInstance의 상태확인
        for(Seat seat : seatList) {
            seat.checkSeatStatus(seatInstanceIdList, Status.FREE);
        }

        //3. Free면 Id와 status응답
        return ProcessSeatCheckQuery.from(seatInstanceIdList, true);
    }

    // TODO : 추후 배치로 고도화 고려 vs Kafka Consumer
    @Scheduled(cron = "0 42 12 * * *", zone = "Asia/Seoul")
    public void warmUpSeatInstance() {

        //Mock Data
        Map<UUID, LocalDateTime> todayTicketOpen = new HashMap<>();
        todayTicketOpen.put(UUID.fromString("2d6d2381-a295-46fa-b798-a891f523c726"), LocalDateTime.now());
        todayTicketOpen.put(UUID.fromString("52aab2c6-82e4-4c10-b983-e26bdb8550de"), LocalDateTime.now().minusMinutes(10));

        List<UUID> concertSequenceIdList = new ArrayList<>(todayTicketOpen.keySet());

        List<Seat> seats = seatRepository.findSeatListWithSeatInstanceInConcertSequenceIdList(concertSequenceIdList);

        List<Object> results = stringRedisTemplate.executePipelined((RedisCallback<Object>) connection -> {
            StringRedisConnection stringConn = (StringRedisConnection) connection;

            for (Seat seat : seats) {
                for (SeatInstance seatInstance : seat.getSeatInstances()) {
                    String basicKey = "SEAT_INSTANCE:" + seatInstance.getConcertSequenceId() + ":" + seatInstance.getId();
                    stringConn.hSet(basicKey, "status", seatInstance.getStatus().getValue());
                    stringConn.hSet(basicKey, "userId", "");
                    stringConn.hSet(basicKey, "reservationId", "");
                    stringConn.hSet(basicKey, "concertId", seatInstance.getConcertId().toString());
                    stringConn.hSet(basicKey, "col", seat.getCol().toString());
                    stringConn.hSet(basicKey, "row", seat.getRow().toString());
                    stringConn.hSet(basicKey, "section", seat.getSection().getValue());
                    stringConn.hSet(basicKey, "floor", seat.getFloor().toString());
                    stringConn.hSet(basicKey, "grade", seatInstance.getGrade().getValue());
                    stringConn.hSet(basicKey, "price", seatInstance.getPrice().toString());
                    stringConn.hSet(basicKey, "expireAt", CommonUtil.LocalDateTimetoString(todayTicketOpen.get(seatInstance.getConcertSequenceId())));
                    stringConn.hSet(basicKey, "updatedAt", "");

                    String sectionKey = "SECTION_LIST:" + seatInstance.getConcertId() + ":" + seatInstance.getConcertSequenceId() + ":" + seat.getSection().getValue();
                    stringConn.hIncrBy(sectionKey, "remainingSeat", 1);
                    stringConn.hSet(sectionKey, "floor", seat.getFloor().toString());
                    stringConn.hSet(sectionKey, "grade", seatInstance.getGrade().getValue());
                    stringConn.hSet(sectionKey, "price", seatInstance.getPrice().toString());

                    String seatIndexKey = "SEAT_INDEX:" + seatInstance.getConcertSequenceId() + ":" + seat.getSection().getValue();
                    stringConn.zAdd(seatIndexKey, seat.getRow() * 100 + seat.getCol(), String.valueOf(seatInstance.getId()));
                }
            }

            return null;
        });

    }

    public String preemptSeat(SeatPreemptCommand seatPreemptCommand, String passport) {

        PassportDto passportDto = passportUtil.getPassportDto(passport);
        Long userId = passportDto.getUserId();

        List<UUID> seatInstanceIdList = seatPreemptCommand.seatList().stream()
                .map(SeatPreemptCommand.SeatRequest::seatInstanceId)
                .toList();

        //Lua 파라미터 준비
        String seatInstancePrefix = "SEAT_INSTANCE:" + seatPreemptCommand.concertSequenceId() + ":";

        List<String> seatKeys = seatInstanceIdList.stream()
                .map(seatId -> seatInstancePrefix + seatId)
                .collect(Collectors.toList());

        UUID reservationId = UUID.randomUUID();
        long ttlMs = Duration.ofMinutes(10).toMillis();

        log.info("Lua Script 실행");

        //TODO : 조회 관련된 인덱스 작업 2개 추가 (ZSET, SEATLIST)
        //Lua Script 실행
        Object result = stringRedisTemplate.execute(
                seatPreemptScript,
                seatKeys,
                userId.toString(),
                reservationId.toString(),
                String.valueOf(ttlMs),
                LocalDateTime.now().toString()
        );

        if ("OK".equals(result)) {
            // 선점 성공 -> outbox 메세지 저장
            log.info("preempt seat successful");
            saveOutbox(seatInstanceIdList,
                    ReservationCreateEvent.builder()
                            .passportDto(passportDto)
                            .userName(seatPreemptCommand.userName())
                            .reservationId(reservationId)
                            .build(),
                    EventType.RESERVATION_CREATE);

        } else if (result instanceof Number && ((Number) result).longValue() == 0) {
            // 직접 return 0 한 경우 (이미 선점 존재 등)
            log.warn("이미 선점된 좌석입니다.");
            throw new SeatUnavailableException();

        } else {
            // nil 반환 (SET 실패 등)
            log.error("redis set error");
            throw new RedisException();
        }

        return reservationId.toString();
    }

    @Transactional
    protected <T extends EventPayload> void saveOutbox(List<UUID> seatInstanceIdList,
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
