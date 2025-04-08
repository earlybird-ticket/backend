package com.earlybird.ticket.venue.application.dto.response;

import lombok.Builder;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

@Builder
public record SeatListQuery(
        UUID concertId,
        UUID concertSequenceId,
        String section,
        String grade,
        int floor,
        List<SeatQuery> seatList
) {
    @Builder
    public record SeatQuery(
            UUID seatId,
            int row,
            int col,
            String seatStatus,
            BigDecimal price
    ) {

    }
}
