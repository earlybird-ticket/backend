package com.earlybird.ticket.venue.application;

import com.earlybird.ticket.venue.domain.dto.WarmupSeatResult;
import java.time.LocalDateTime;
import java.util.List;

public interface SeatWarmupCacheLoader {

    void load(
        List<WarmupSeatResult> seats, LocalDateTime ticketDeadline, LocalDateTime vipTicketDeadline
    );

}
