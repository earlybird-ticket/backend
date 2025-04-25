package com.earlybird.ticket.venue.application.event.handler;

import com.earlybird.ticket.common.entity.EventPayload;
import com.earlybird.ticket.common.entity.constant.Code;
import com.earlybird.ticket.common.util.CommonUtil;
import com.earlybird.ticket.venue.application.event.dto.request.*;
import com.earlybird.ticket.venue.application.event.dto.response.*;
import com.earlybird.ticket.venue.common.event.EventType;
import com.earlybird.ticket.venue.common.exception.SeatUnavailableException;
import com.earlybird.ticket.venue.common.util.EventConverter;
import com.earlybird.ticket.venue.common.exception.SeatNotFoundException;
import com.earlybird.ticket.venue.common.exception.TimeOutException;
import com.earlybird.ticket.venue.common.util.RedisKeyFactory;
import com.earlybird.ticket.venue.domain.entity.Event;
import com.earlybird.ticket.venue.domain.entity.Outbox;
import com.earlybird.ticket.venue.domain.entity.Seat;
import com.earlybird.ticket.venue.domain.entity.constant.Section;
import com.earlybird.ticket.venue.domain.repository.OutboxRepository;
import com.earlybird.ticket.venue.domain.repository.SeatRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RBucket;
import org.redisson.api.RMap;
import org.redisson.api.RedissonClient;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class SeatHandlerServiceImpl implements SeatHandlerService {
    private final SeatRepository seatRepository;
    private final OutboxRepository outboxRepository;
    private final EventConverter eventConverter;
    private final RedissonClient redissonClient;
    private final RedisKeyFactory redisKeyFactory;

    @Override
    @Transactional
    public void createSeat(SeatCreatePayload seatCreatePayload) {

        List<Seat> seatList = new ArrayList<>();

        for (SeatCreatePayload.SeatInfo seatInfo : seatCreatePayload.seatList()) {
            seatList.addAll(Seat.create(seatInfo.rowCnt(),
                                        seatInfo.colCnt(),
                                        seatInfo.section(),
                                        seatInfo.floor(),
                                        seatCreatePayload.venueId(),
                                        seatCreatePayload.hallId(),
                                        seatCreatePayload.passportDto()
                                                         .getUserId()));
        }

        seatRepository.saveAll(seatList);
    }

    @Override
    @Transactional
    public void createSeatInstance(SeatInstanceCreatePayload seatInstanceCreatePayload) {

        List<Seat> seatList = seatRepository.findSeatByHallId(seatInstanceCreatePayload.hallId());

        checkSeatListExists(seatList);

        for (UUID concertSequenceId : seatInstanceCreatePayload.concertSequenceList()) {
            for (Seat seat : seatList) {
                for (SeatInstanceCreatePayload.SeatInstanceInfo info : seatInstanceCreatePayload.seatInstanceInfoList()) {

                    if (seat.getSection()
                            .equals(Section.getByValue(info.section()))) {
                        seat.createSeatInstance(info.grade(),
                                                info.price(),
                                                concertSequenceId,
                                                seatInstanceCreatePayload.venueId(),
                                                seatInstanceCreatePayload.concertId(),
                                                seatInstanceCreatePayload.hallId(),
                                                seatInstanceCreatePayload.passportDto()
                                                                         .getUserId());
                    }
                }
            }
        }

    }

    @Override
    @Transactional
    public void updateSeatInstance(SeatInstanceUpdatePayload seatInstanceUpdatePayload) {

        Seat seat = seatRepository.findSeatBySeatInstanceId(seatInstanceUpdatePayload.seatInstanceId());

        checkSeatExist(seat);

        seat.updateSeatInstance(seatInstanceUpdatePayload.seatInstanceId(),
                                seatInstanceUpdatePayload.hallId(),
                                seatInstanceUpdatePayload.concertId(),
                                seatInstanceUpdatePayload.concertSequenceId(),
                                seatInstanceUpdatePayload.grade(),
                                seatInstanceUpdatePayload.status(),
                                seatInstanceUpdatePayload.price(),
                                seatInstanceUpdatePayload.passportDto()
                                                         .getUserId());
    }

    @Override
    @Transactional
    public void deleteSeatInstance(SeatInstanceDeletePayload seatInstanceDeletePayload) {

        Seat seat = seatRepository.findSeatBySeatInstanceId(seatInstanceDeletePayload.seatInstanceId());

        checkSeatExist(seat);

        seat.deleteSeatInstance(seatInstanceDeletePayload.seatInstanceId(),
                                seatInstanceDeletePayload.passportDto()
                                                         .getUserId());
    }

    @Override
    @Transactional
    public void confirmSeat(SeatConfirmPayload seatConfirmPayload) {

        List<UUID> seatInstanceIdList = seatConfirmPayload.seatInstanceIdList();
        Long userId = seatConfirmPayload.passportDto().getUserId();

        List<String> seatKeys = seatInstanceIdList.stream()
                .map(seatInstanceId -> redisKeyFactory.generateSeatInstanceKey(
                                seatConfirmPayload.concertSequenceId(),
                                seatInstanceId
                        )
                )
                .toList();

        try {

            for(String seatKey : seatKeys) {
                RMap<String, String> map = redissonClient.getMap(seatKey);
                String storedUserId = map.get("userId");

                if(storedUserId == null ||!storedUserId.equals(userId.toString())) {
                    throw new SeatUnavailableException();
                }

                if(!"PREEMPTED".equals(map.get("status"))) {
                    throw new SeatUnavailableException();
                }

                map.put("status", "CONFIRMED");
                map.put("updatedAt",  CommonUtil.LocalDateTimetoString(LocalDateTime.now()));
            }

            saveOutbox(seatInstanceIdList,
                       SeatConfirmSuccessEvent.builder()
                                              .passportDto(seatConfirmPayload.passportDto())
                                              .seatInstanceIdList(seatInstanceIdList)
                                              .build(),
                       EventType.SEAT_CONFIRM_SUCCESS);

        } catch (Exception e) {
            log.error("메시지 처리 실패: {}",
                      e.getMessage());

            saveOutbox(seatInstanceIdList,
                       SeatConfirmFailEvent.builder()
                                           .passportDto(seatConfirmPayload.passportDto())
                                           .seatInstanceIdList(seatInstanceIdList)
                                           .code(Code.SEAT_CONFIRM_FAIL.getCode())
                                           .build(),
                       EventType.SEAT_CONFIRM_FAIL);
        }
    }

    @Override
    @Transactional
    public void returnSeat(SeatReturnPayload seatReturnPayload) {

        List<UUID> seatInstanceIdList = seatReturnPayload.seatInstanceIdList();

        try {
            //TODO : redis에 해당 좌석 상태 update
            //1. seatInstance 가져오기
            List<Seat> seatList = getSeatList(seatInstanceIdList);

            // 3. SeatInstance의 상태확인
            // Free update
            for (Seat seat : seatList) {
                seat.returnSeat(seatInstanceIdList,
                                seatReturnPayload.passportDto()
                                                 .getUserId());
            }
            //4. 저장
            //5. 아웃 박스 저장
            saveOutbox(seatInstanceIdList,
                       SeatReturnSuccessEvent.builder()
                                             .passportDto(seatReturnPayload.passportDto())
                                             .seatInstanceIdList(seatInstanceIdList)
                                             .build(),
                       EventType.SEAT_RETURN_SUCCESS);
        } catch (Exception e) {
            log.error("메시지 처리 실패: {}",
                      e.getMessage());

            saveOutbox(seatInstanceIdList,
                       SeatReturnFailEvent.builder()
                                          .passportDto(seatReturnPayload.passportDto())
                                          .seatInstanceIdList(seatInstanceIdList)
                                          .code(Code.SEAT_RETURN_FAIL.getCode())
                                          .build(),
                       EventType.SEAT_RETURN_FAIL);
        }

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

    private void checkExpiredReservationTime(UUID reservationId) {
        RBucket<String> bucket = redissonClient.getBucket(RedisKeyFactory.RESERVATION_TIME_LIMIT_PREFIX + reservationId);

        if (!bucket.isExists()) {
            throw new TimeOutException();
        }
    }

    private List<Seat> getSeatList(List<UUID> seatInstanceIdList) {
        // 1. seatInstanceId와 일치하는 seat 가져오기
        List<Seat> seatList = seatRepository.findSeatListWithSeatInstanceInSeatInstanceIdList(seatInstanceIdList);

        // 2. seat이 다 존재하는 지 확인
        if (seatList.size() != seatInstanceIdList.size()) {
            throw new SeatNotFoundException();
        }
        return seatList;
    }

    private void checkSeatExist(Seat seat) {
        if (seat == null) {
            throw new SeatNotFoundException();
        }
    }

    private void checkSeatListExists(List<Seat> seatList) {
        if (seatList.isEmpty()) {
            throw new SeatNotFoundException();
        }
    }
}
