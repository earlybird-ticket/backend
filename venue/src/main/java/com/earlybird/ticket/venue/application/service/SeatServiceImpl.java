package com.earlybird.ticket.venue.application.service;

import com.earlybird.ticket.venue.application.dto.request.ProcessSeatCheckCommand;
import com.earlybird.ticket.venue.application.dto.response.ProcessSeatCheckQuery;
import com.earlybird.ticket.venue.application.dto.response.SeatListQuery;
import com.earlybird.ticket.venue.application.dto.response.SectionListQuery;
import com.earlybird.ticket.venue.common.exception.SeatNotFoundException;
import com.earlybird.ticket.venue.domain.entity.Seat;
import com.earlybird.ticket.venue.domain.entity.constant.Section;
import com.earlybird.ticket.venue.domain.entity.constant.Status;
import com.earlybird.ticket.venue.domain.repository.SeatRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
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
}
