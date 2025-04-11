package com.earlybird.ticket.venue.application.service;

import com.earlybird.ticket.common.entity.EventPayload;
import com.earlybird.ticket.common.util.CommonUtil;
import com.earlybird.ticket.common.util.EventPayloadConverter;
import com.earlybird.ticket.venue.application.dto.request.ProcessSeatCheckCommand;
import com.earlybird.ticket.venue.application.dto.response.ProcessSeatCheckQuery;
import com.earlybird.ticket.venue.application.dto.response.SeatListQuery;
import com.earlybird.ticket.venue.application.dto.response.SectionListQuery;
import com.earlybird.ticket.venue.application.event.dto.request.*;
import com.earlybird.ticket.venue.application.event.dto.response.SeatConfirmSuccessEvent;
import com.earlybird.ticket.venue.application.event.dto.response.SeatPreemptSuccessEvent;
import com.earlybird.ticket.venue.common.event.EventType;
import com.earlybird.ticket.venue.common.exception.SeatNotFoundException;
import com.earlybird.ticket.venue.domain.entity.Event;
import com.earlybird.ticket.venue.domain.entity.Outbox;
import com.earlybird.ticket.venue.domain.entity.Seat;
import com.earlybird.ticket.venue.domain.entity.constant.Section;
import com.earlybird.ticket.venue.application.event.dto.request.SeatCreatePayload.SeatInfo;
import com.earlybird.ticket.venue.application.event.dto.request.SeatInstanceCreatePayload.SeatInstanceInfo;
import com.earlybird.ticket.venue.domain.entity.constant.Status;
import com.earlybird.ticket.venue.domain.repository.OutboxRepository;
import com.earlybird.ticket.venue.domain.repository.SeatRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static java.time.LocalTime.now;

@Service
@RequiredArgsConstructor
public class SeatServiceImpl implements SeatService {

    private final SeatRepository seatRepository;
    private final OutboxRepository outboxRepository;
    private final EventPayloadConverter eventPayloadConverter;

    @Override
    public SectionListQuery findSectionList(UUID concertSequenceId) {
        return SectionListQuery
                .from(seatRepository.findSectionList(concertSequenceId));
    }

    @Override
    public SeatListQuery findSeatList(UUID concertSequenceId, String section) {
        return SeatListQuery
                .from(seatRepository.findSeatList(
                        concertSequenceId,
                        Section.getByValue(section)
                ));
    }

    @Override
    public ProcessSeatCheckQuery checkSeat(ProcessSeatCheckCommand processSeatCheckCommand) {
        List<UUID> seatInstanceIdList = processSeatCheckCommand.seatInstanceIdList();
        // 1. seatInstanceId와 일치하는 seat 가져오기
        List<Seat> seatList = seatRepository.findSeatListWithSeatInstanceInSeatInstanceIdList(seatInstanceIdList);

        // 2. seat이 다 존재하는 지 확인
        if(seatList.size() != seatInstanceIdList.size()) {
            throw new SeatNotFoundException();
        }

        // 3. SeatInstance의 상태확인
        for(Seat seat : seatList) {
            seat.checkSeatStatus(seatInstanceIdList, Status.FREE);
        }

        //3. Free면 Id와 status응답
        return ProcessSeatCheckQuery.from(seatInstanceIdList, true);
    }

    @Override
    @Transactional
    public void createSeat(SeatCreatePayload seatCreatePayload) {
        //0. userId 가져오기
        //1. seatList size 만큼 반복
        //2. row / col for문 돌면서 seatList 생성
        List<Seat> seatList = new ArrayList<>();

        for(SeatInfo seatInfo : seatCreatePayload.seatList()) {
            seatList = Seat.create(
                    seatInfo,
                    seatCreatePayload.venueId(),
                    seatCreatePayload.venueId(),
                    seatCreatePayload.passportDto().getUserId()
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
                for (SeatInstanceInfo info : seatInstanceCreatePayload.seatInstanceInfoList()) {
                    //3. 좌석 섹션에 따라 좌석 정보 + Payload 정보로 seatInstance 생성
                    if (seat.getSection().equals(Section.getByValue(info.section()))) {
                        seat.createSeatInstance(
                                info,
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

        // 1. seatInstanceId와 일치하는 seat 가져오기
        List<Seat> seatList = seatRepository.findSeatListWithSeatInstanceInSeatInstanceIdList(seatInstanceIdList);

        // 2. seat이 다 존재하는 지 확인
        if(seatList.size() != seatInstanceIdList.size()) {
            throw new SeatNotFoundException();
        }

        // 3. SeatInstance의 상태확인
        // Free 상태면 Preempt로 update 후 응답
        for(Seat seat : seatList) {
            seat.preemptSeat(seatInstanceIdList, seatPreemptPayload.passportDto().getUserId());
        }

        //4. 저장
        //5. 아웃 박스 저장
        saveOutbox(seatInstanceIdList,
                SeatPreemptSuccessEvent.builder()
                        .seatInstanceIdList(seatInstanceIdList)
                        .build(),
                EventType.SEAT_PREEMPT_SUCCESS
        );

    }

    @Override
    @Transactional
    public void confirmSeat(SeatConfirmPayload seatConfirmPayload) {

        List<UUID> seatInstanceIdList = seatConfirmPayload.seatInstanceIdList();
        //1. seatInstance 가져오기
        List<Seat> seatList = seatRepository.findSeatListWithSeatInstanceInSeatInstanceIdList(seatInstanceIdList);

        // 2. seat이 다 존재하는 지 확인
        if(seatList.size() != seatInstanceIdList.size()) {
            throw new SeatNotFoundException();
        }

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
                        .seatInstanceIdList(seatInstanceIdList)
                        .build(),
                EventType.SEAT_CONFIRM_SUCCESS
        );
    }

    @Override
    @Transactional
    public void returnSeat(SeatReturnPayload seatReturnPayload) {
        //0. userId 가져오기
        //1. seatInstance 가져오기
        //2. Free로 update
        //3. 저장
        //4. 아웃박스 저장
    }

    private <T extends EventPayload> void saveOutbox(
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
                .payload(eventPayloadConverter.serializePayload((EventPayload) event))
                .build()
        );
    }
}
