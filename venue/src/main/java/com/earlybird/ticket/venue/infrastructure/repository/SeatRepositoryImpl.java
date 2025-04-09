package com.earlybird.ticket.venue.infrastructure.repository;

import com.earlybird.ticket.venue.domain.dto.SeatListResult;
import com.earlybird.ticket.venue.domain.dto.SectionListResult;
import com.earlybird.ticket.venue.domain.entity.constant.Section;
import com.earlybird.ticket.venue.domain.repository.SeatRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
@RequiredArgsConstructor
public class SeatRepositoryImpl implements SeatRepository {
    private final SeatJpaRepository seatJpaRepository;
    private final SeatQueryRepository seatQueryRepository;

    @Override
    public SectionListResult findSectionList(UUID concertSequenceId) {
        return seatQueryRepository.findSectionList(concertSequenceId);
    }

    @Override
    public SeatListResult findSeatList(UUID concertSequenceId, Section section) {
        return seatQueryRepository.findSeatList(concertSequenceId, section);
    }
}
