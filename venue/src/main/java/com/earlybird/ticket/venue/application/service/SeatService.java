package com.earlybird.ticket.venue.application.service;

import com.earlybird.ticket.venue.application.dto.request.ProcessSeatCheckCommand;
import com.earlybird.ticket.venue.application.dto.request.SeatPreemptCommand;
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

    String preemptSeat(SeatPreemptCommand seatPreemptCommand, String passport);

    String preemptSeatByVIP(SeatPreemptCommand seatPreemptCommand, String passport);

    String preemptWaitingSeatByVIP(SeatPreemptCommand seatPreemptCommand, String passport);
}
