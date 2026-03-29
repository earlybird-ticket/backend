package com.earlybird.ticket.venue.application.service;

import com.earlybird.ticket.venue.application.dto.request.ProcessSeatCheckCommand;
import com.earlybird.ticket.venue.application.dto.request.SeatPreemptCommand;
import com.earlybird.ticket.venue.application.dto.response.ProcessSeatCheckQuery;
import com.earlybird.ticket.venue.application.dto.response.SeatListQueryV2;
import com.earlybird.ticket.venue.application.dto.response.SectionListQuery;
import com.earlybird.ticket.venue.domain.dto.WarmupSeatResult;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import org.springframework.stereotype.Service;

@Service
public interface SeatService {
    SectionListQuery findSectionList(UUID concertSequenceId);

    SeatListQueryV2 findSeatList(UUID concertSequenceId, String section);

    ProcessSeatCheckQuery checkSeat(ProcessSeatCheckCommand processSeatCheckCommand);

    String preemptSeat(SeatPreemptCommand seatPreemptCommand, String passport);

    String preemptSeatByVIP(SeatPreemptCommand seatPreemptCommand, String passport);

    String preemptWaitingSeatByVIP(SeatPreemptCommand seatPreemptCommand, String passport);

    Map<UUID, List<WarmupSeatResult>> collectWarmupSeatData(List<UUID> concertSequenceIds);
}
