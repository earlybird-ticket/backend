package com.earlybird.ticket.venue.application.service;

import com.earlybird.ticket.venue.application.dto.response.SeatListQuery;
import com.earlybird.ticket.venue.application.dto.response.SectionListQuery;
import com.earlybird.ticket.venue.domain.entity.constant.Section;
import com.earlybird.ticket.venue.domain.repository.SeatRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

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
}
