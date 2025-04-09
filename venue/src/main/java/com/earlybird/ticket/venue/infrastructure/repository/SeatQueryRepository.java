package com.earlybird.ticket.venue.infrastructure.repository;

import com.earlybird.ticket.venue.domain.dto.SeatListResult;
import com.earlybird.ticket.venue.domain.dto.SectionListResult;
import com.earlybird.ticket.venue.domain.entity.constant.Section;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface SeatQueryRepository {
    SectionListResult findSectionList(UUID concertSequenceId);

    SeatListResult findSeatList(UUID concertSequenceId, Section section);
}
