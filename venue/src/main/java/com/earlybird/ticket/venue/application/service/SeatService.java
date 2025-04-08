package com.earlybird.ticket.venue.application.service;

import com.earlybird.ticket.venue.application.dto.response.SectionListQuery;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public interface SeatService {
    SectionListQuery findSectionList(UUID concertSequenceId);
}
