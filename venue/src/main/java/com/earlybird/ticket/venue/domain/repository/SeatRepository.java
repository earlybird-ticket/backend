package com.earlybird.ticket.venue.domain.repository;

import com.earlybird.ticket.venue.domain.dto.SectionListResult;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface SeatRepository {
    SectionListResult findSectionList(UUID concertSequenceId);
}
