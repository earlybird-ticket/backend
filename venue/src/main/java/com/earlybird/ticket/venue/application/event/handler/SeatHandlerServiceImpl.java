package com.earlybird.ticket.venue.application.event.handler;

import com.earlybird.ticket.common.entity.EventPayload;
import com.earlybird.ticket.common.entity.constant.Code;
import com.earlybird.ticket.common.util.CommonUtil;
import com.earlybird.ticket.venue.application.event.dto.request.*;
import com.earlybird.ticket.venue.application.event.dto.response.*;
import com.earlybird.ticket.venue.common.event.EventType;
import com.earlybird.ticket.venue.common.event.util.EventConverter;
import com.earlybird.ticket.venue.common.exception.SeatNotFoundException;
import com.earlybird.ticket.venue.common.exception.TimeOutException;
import com.earlybird.ticket.venue.domain.entity.Event;
import com.earlybird.ticket.venue.domain.entity.Outbox;
import com.earlybird.ticket.venue.domain.entity.Seat;
import com.earlybird.ticket.venue.domain.entity.constant.Section;
import com.earlybird.ticket.venue.domain.repository.OutboxRepository;
import com.earlybird.ticket.venue.domain.repository.SeatRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RedissonClient;
import org.springframework.data.redis.core.StringRedisTemplate;
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

    private final String timeCachePrefix = "TIME_LIMIT:RESERVATION_ID:";

    @Override
    @Transactional
    public void createSeat(SeatCreatePayload seatCreatePayload) {
        //0. userId 가져오기
        //1. seatList size 만큼 반복
        //2. row / col for문 돌면서 seatList 생성
        List<Seat> seatList = new ArrayList<>();

        for(SeatCreatePayload.SeatInfo seatInfo : seatCreatePayload.seatList()) {
            seatList.addAll(
                    Seat.create(
                    seatInfo.rowCnt(),
                    seatInfo.colCnt(),
                    seatInfo.section(),
                    seatInfo.floor(),
                    seatCreatePayload.venueId(),
                    seatCreatePayload.venueId(),
                    seatCreatePayload.passportDto().getUserId()
                    )
            );
        }
        //3. seatList 저장
        seatRepository.saveAll(seatList);
    }

    @Override
    @Transactional
    public void createSeatInstance(SeatInstanceCreatePayload seatInstanceCreatePayload) {
        //0. userId 가져오기
        //1. hallId가 같은 모든 좌석 조회
        List<Seat> seatList = seatRepository.findSeatByHallId(seatInstanceCreatePayload.hallId());

        //2. 모든 좌석에 대해서 concertSequenceList만큼 반복
        for (UUID concertSequenceId : seatInstanceCreatePayload.concertSequenceList()) {
            for (Seat seat : seatList) {
                for (SeatInstanceCreatePayload.SeatInstanceInfo info : seatInstanceCreatePayload.seatInstanceInfoList()) {
                    //3. 좌석 섹션에 따라 좌석 정보 + Payload 정보로 seatInstance 생성
                    if (seat.getSection().equals(Section.getByValue(info.section()))) {
                        seat.createSeatInstance(
                                info.grade(),
                                info.price(),
                                concertSequenceId,
                                seatInstanceCreatePayload.venueId(),
                                seatInstanceCreatePayload.concertId(),
                                seatInstanceCreatePayload.hallId(),
                                seatInstanceCreatePayload.passportDto().getUserId()
                        );
                    }
                }
            }
        }
        //4. seat 저장
    }

    @Override
    @Transactional
    public void updateSeatInstance(SeatInstanceUpdatePayload seatInstanceUpdatePayload) {
        //1. seatInstance 가져오기
        Seat seat = seatRepository.findSeatBySeatInstanceId(seatInstanceUpdatePayload.seatInstanceId());

        if(seat == null) {
            throw new SeatNotFoundException();
        }

        //2. seatInstance 업데이트
        seat.updateSeatInstance(
                seatInstanceUpdatePayload.seatInstanceId(),
                seatInstanceUpdatePayload.hallId(),
                seatInstanceUpdatePayload.concertId(),
                seatInstanceUpdatePayload.concertSequenceId(),
                seatInstanceUpdatePayload.grade(),
                seatInstanceUpdatePayload.status(),
                seatInstanceUpdatePayload.price(),
                seatInstanceUpdatePayload.passportDto().getUserId()
        );
        //3. 저장
    }

    @Override
    @Transactional
    public void deleteSeatInstance(SeatInstanceDeletePayload seatInstanceDeletePayload) {
        //1. seatInstance 가져오기
        Seat seat = seatRepository.findSeatBySeatInstanceId(seatInstanceDeletePayload.seatInstanceId());
        //2. seatInstance delete 업데이트
        seat.deleteSeatInstance(
                seatInstanceDeletePayload.seatInstanceId(),
                seatInstanceDeletePayload.passportDto().getUserId()
        );
        //3. 저장
    }

    @Override
    @Transactional
    public void preemptSeat(SeatPreemptPayload seatPreemptPayload) {

        List<UUID> seatInstanceIdList = seatPreemptPayload.seatInstanceIdList();

        try {
            List<Seat> seatList = getSeatList(seatInstanceIdList);

            // 3. SeatInstance의 상태확인
            // Free 상태면 Preempt로 update 후 응답
            for(Seat seat : seatList) {
                seat.preemptSeat(seatInstanceIdList, seatPreemptPayload.passportDto().getUserId());
            }

            //4. 저장
            //5. 아웃 박스 저장
            saveOutbox(seatInstanceIdList,
                    SeatPreemptSuccessEvent.builder()
                            .passportDto(seatPreemptPayload.passportDto())
                            .seatInstanceIdList(seatInstanceIdList)
                            .build(),
                    EventType.SEAT_PREEMPT_SUCCESS
            );
        } catch (Exception e) {
            log.error("메시지 처리 실패: {}", e.getMessage());

            saveOutbox(
                    seatInstanceIdList,
                    SeatPreemptFailEvent.builder()
                            .passportDto(seatPreemptPayload.passportDto())
                            .seatInstanceIdList(seatInstanceIdList)
                            .code(Code.SEAT_PREEMPT_FAIL.getCode())
                            .build(),
                    EventType.SEAT_PREEMPT_FAIL
            );
        }


    }

    @Override
    @Transactional
    public void confirmSeat(SeatConfirmPayload seatConfirmPayload) {

        List<UUID> seatInstanceIdList = seatConfirmPayload.seatInstanceIdList();

        try{
            if(!redissonClient.getBucket(timeCachePrefix).isExists()) {
                throw new TimeOutException();
            }

            //1. seatInstance 가져오기
            List<Seat> seatList = getSeatList(seatInstanceIdList);

            // 3. SeatInstance의 상태확인
            // Preempt 상태면 Confirm으로 update 후 응답
            for(Seat seat : seatList) {
                seat.confirmSeat(seatInstanceIdList, seatConfirmPayload.passportDto().getUserId());
            }
            //4. 저장
            //5. 아웃 박스 저장
            saveOutbox(
                    seatInstanceIdList,
                    SeatConfirmSuccessEvent.builder()
                            .passportDto(seatConfirmPayload.passportDto())
                            .seatInstanceIdList(seatInstanceIdList)
                            .build(),
                    EventType.SEAT_CONFIRM_SUCCESS
            );

        } catch (Exception e) {
            log.error("메시지 처리 실패: {}", e.getMessage());

            saveOutbox(
                    seatInstanceIdList,
                    SeatConfirmFailEvent.builder()
                            .passportDto(seatConfirmPayload.passportDto())
                            .seatInstanceIdList(seatInstanceIdList)
                            .code(Code.SEAT_CONFIRM_FAIL.getCode())
                            .build(),
                    EventType.SEAT_CONFIRM_FAIL
            );
        }
    }

    @Override
    @Transactional
    public void returnSeat(SeatReturnPayload seatReturnPayload) {

        List<UUID> seatInstanceIdList = seatReturnPayload.seatInstanceIdList();

        try {
            //1. seatInstance 가져오기
            List<Seat> seatList = getSeatList(seatInstanceIdList);

            // 3. SeatInstance의 상태확인
            // Free update
            for(Seat seat : seatList) {
                seat.returnSeat(seatInstanceIdList, seatReturnPayload.passportDto().getUserId());
            }
            //4. 저장
            //5. 아웃 박스 저장
            saveOutbox(
                    seatInstanceIdList,
                    SeatReturnSuccessEvent.builder()
                            .passportDto(seatReturnPayload.passportDto())
                            .seatInstanceIdList(seatInstanceIdList)
                            .build(),
                    EventType.SEAT_RETURN_SUCCESS
            );
        } catch (Exception e) {
            log.error("메시지 처리 실패: {}", e.getMessage());

            saveOutbox(
                    seatInstanceIdList,
                    SeatReturnFailEvent.builder()
                            .passportDto(seatReturnPayload.passportDto())
                            .seatInstanceIdList(seatInstanceIdList)
                            .code(Code.SEAT_RETURN_FAIL.getCode())
                            .build(),
                    EventType.SEAT_RETURN_FAIL
            );
        }

    }

    @Transactional
    protected <T extends EventPayload> void saveOutbox(
            List<UUID> seatInstanceIdList,
            T eventPayload,
            EventType eventType
    ) {
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
                .build()
        );
    }

    private List<Seat> getSeatList(List<UUID> seatInstanceIdList) {
        // 1. seatInstanceId와 일치하는 seat 가져오기
        List<Seat> seatList = seatRepository.findSeatListWithSeatInstanceInSeatInstanceIdList(seatInstanceIdList);

        // 2. seat이 다 존재하는 지 확인
        if(seatList.size() != seatInstanceIdList.size()) {
            throw new SeatNotFoundException();
        }
        return seatList;
    }
}
