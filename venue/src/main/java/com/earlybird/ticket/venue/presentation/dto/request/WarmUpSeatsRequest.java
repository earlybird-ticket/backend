package com.earlybird.ticket.venue.presentation.dto.request;

import com.earlybird.ticket.venue.application.dto.request.WarmupSeatsCommand;
import com.earlybird.ticket.venue.application.dto.request.WarmupSeatsCommand.ConcertDeadLine;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

public record WarmUpSeatsRequest(
    UUID concertSequenceId,
    LocalDateTime ticketExpiredAt,
    LocalDateTime vipTicketExpiredAt
) {

    public static WarmupSeatsCommand toWarmupSeatCommand(List<WarmUpSeatsRequest> requests) {
        return WarmupSeatsCommand.builder()
            .concertDeadLines(
                requests.stream()
                    .map(
                        seat -> ConcertDeadLine.builder()
                            .concertSequenceId(seat.concertSequenceId)
                            .ticketExpiredAt(seat.ticketExpiredAt)
                            .vipTicketExpiredAt(seat.vipTicketExpiredAt)
                            .build()
                    )
                    .toList()
            ).build();

    }

}
