package com.earlybird.ticket.venue.presentation.dto.request;

import com.earlybird.ticket.venue.application.dto.request.WarmupSeatsCommand;
import com.earlybird.ticket.venue.application.dto.request.WarmupSeatsCommand.ConcertDeadLine;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import lombok.Builder;

public record WarmUpSeatsRequest(
    List<WarmUpSeatRequest> seats
) {

    @Builder
    record WarmUpSeatRequest(
        UUID concertSequenceId,
        LocalDateTime ticketExpiredAt,
        LocalDateTime vipTicketExpiredAt
    ) {

    }

    public WarmupSeatsCommand toWarmupSeatCommand() {
        return WarmupSeatsCommand.builder()
            .concertDeadLines(seats.stream().map(
                    seat -> ConcertDeadLine
                        .builder()
                        .concertSequenceId(seat.concertSequenceId)
                        .ticketExpiredAt(seat.ticketExpiredAt)
                        .vipTicketExpiredAt(seat.vipTicketExpiredAt)
                        .build()
                ).toList()
            )
            .build();
    }

}
