package com.earlybird.ticket.admin.application.dto;

import java.util.List;
import java.util.UUID;
import lombok.Builder;

@Builder
public record RegisterSeatCommand(
        UUID hallId,
        UUID venueId,
        List<SeatInfo> seatList
) {

    @Builder
    public record SeatInfo(
            String section,
            Integer rowCnt,
            Integer colCnt,
            Integer floor

    ) {

    }
}
