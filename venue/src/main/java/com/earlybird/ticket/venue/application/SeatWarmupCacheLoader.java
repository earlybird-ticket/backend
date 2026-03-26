package com.earlybird.ticket.venue.application;

import com.earlybird.ticket.venue.domain.dto.WarmupSeatResult;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public interface SeatWarmupCacheLoader {

    void load(
        List<WarmupSeatResult> seats,
        Map<UUID, LocalDateTime> ticketDeadline,
        Map<UUID, LocalDateTime> vipTicketDeadline
    );

}
