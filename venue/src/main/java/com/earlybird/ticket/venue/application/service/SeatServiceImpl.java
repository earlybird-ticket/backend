package com.earlybird.ticket.venue.application.service;

import com.earlybird.ticket.venue.application.dto.request.ProcessSeatCheckCommand;
import com.earlybird.ticket.venue.application.dto.response.ProcessSeatCheckQuery;
import com.earlybird.ticket.venue.application.dto.response.SeatListQuery;
import com.earlybird.ticket.venue.application.dto.response.SectionListQuery;
import com.earlybird.ticket.venue.application.event.dto.request.*;
import com.earlybird.ticket.venue.domain.entity.constant.Section;
import com.earlybird.ticket.venue.domain.repository.SeatRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class SeatServiceImpl implements SeatService {

    private final SeatRepository seatRepository;

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
        //1. seatInstanceId와 일치하는 seat과 seatInstance 가져오기
        //2. 상태 확인 후 Free가 아니면 예외
        //3. Free면 Id와 status응답
        return null;
    }

    @Override
    @Transactional
    public void createSeat(SeatCreatePayload seatCreatePayload) {
        //0. userId 가져오기
        //1. seatList size 만큼 반복
        //2. row / col for문 돌면서 seatList 생성
        //3. seatList 저장
    }

    @Override
    @Transactional
    public void createSeatInstance(SeatInstanceCreatePayload seatInstanceCreatePayload) {
        //0. userId 가져오기
        //1. hallId가 같은 모든 좌석 조회
        //2. 모든 좌석에 대해서 concertSequenceList만큼 반복
        //3. 좌석 섹션에 따라 좌석 정보 + Payload 정보로 seatInstance 생성
        //4. seat 저장
    }

    @Override
    @Transactional
    public void updateSeatInstance(SeatInstanceUpdatePayload seatInstanceUpdatePayload) {
        //0. userId 가져오기
        //1. seatInstance 가져오기
        //2. seatInstance 업데이트
        //3. 저장
    }

    @Override
    @Transactional
    public void deleteSeatInstance(SeatInstanceDeletePayload seatInstanceDeletePayload) {
        //0. userId 가져오기
        //1. seatInstance 가져오기
        //2. seatInstance delete 업데이트
        //3. 저장
    }

    @Override
    @Transactional
    public void preemptSeat(SeatPreemptPayload seatPreemptPayload) {
        //0. userId 가져오기
        //1. seatInstance 가져오기
        //2. Free 상태가 아니면 예외 발생
        //3. Free 상태면 Preempt로 update 후 응답
        //4. 저장
    }

    @Override
    @Transactional
    public void confirmSeat(SeatConfirmPayload seatConfirmPayload) {
        //0. userId 가져오기
        //1. seatInstance 가져오기
        //2. Confirm으로 update
        //3. 저장
    }

    @Override
    @Transactional
    public void returnSeat(SeatReturnPayload seatReturnPayload) {
        //0. userId 가져오기
        //1. seatInstance 가져오기
        //2. Free로 update
        //3. 저장
    }
}
