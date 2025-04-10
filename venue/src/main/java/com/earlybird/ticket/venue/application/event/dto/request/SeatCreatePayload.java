package com.earlybird.ticket.venue.application.event.dto.request;

import com.earlybird.ticket.common.entity.EventPayload;
import com.earlybird.ticket.common.entity.PassportDto;

import java.util.List;
import java.util.UUID;

public record SeatCreatePayload(
        PassportDto passportDto,
        UUID hallId,
        UUID venueId,
        List<SeatInfo> seatList
) implements EventPayload {

    public record SeatInfo(
            String section,
            Integer rowCnt,
            Integer colCnt,
            Integer floor

    ){

    }
}
