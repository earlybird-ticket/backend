package com.earlybird.ticket.venue.application.service;

import com.earlybird.ticket.venue.application.dto.request.ProcessSeatCheckCommand;
import com.earlybird.ticket.venue.application.dto.response.ProcessSeatCheckQuery;
import com.earlybird.ticket.venue.application.dto.response.SeatListQuery;
import com.earlybird.ticket.venue.application.dto.response.SectionListQuery;
import com.earlybird.ticket.venue.application.event.dto.request.*;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public interface SeatService {
    SectionListQuery findSectionList(UUID concertSequenceId);

    SeatListQuery findSeatList(UUID concertSequenceId, String section);

    ProcessSeatCheckQuery checkSeat(ProcessSeatCheckCommand processSeatCheckCommand);

}
