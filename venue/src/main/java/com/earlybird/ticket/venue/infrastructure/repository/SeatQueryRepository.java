package com.earlybird.ticket.venue.infrastructure.repository;

import com.earlybird.ticket.venue.domain.dto.SectionListResult;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface SeatQueryRepository {
    SectionListResult findSectionList(UUID concertSequenceId);
}
