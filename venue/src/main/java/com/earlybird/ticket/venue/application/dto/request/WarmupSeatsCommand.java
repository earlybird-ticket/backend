package com.earlybird.ticket.venue.application.dto.request;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import lombok.Builder;

@Builder
public record WarmupSeatsCommand(
    List<ConcertDeadLine> concertDeadLines
) {

    @Builder
    public record ConcertDeadLine(
        UUID concertSequenceId,
        LocalDateTime ticketExpiredAt,
        LocalDateTime vipTicketExpiredAt
    ) {

    }

}
