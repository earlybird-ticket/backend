package com.earlybird.ticket.venue.application.event.dto.request;

import com.earlybird.ticket.common.entity.EventPayload;
import com.earlybird.ticket.common.entity.PassportDto;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

public record SeatInstanceCreatePayload(
        PassportDto passportDto,
        UUID concertId,
        UUID venueId,
        UUID hallId,
        List<UUID> concertSequenceList,
        List<SeatInstanceInfo> seatInstanceInfoList
) implements EventPayload {

    public record SeatInstanceInfo(
            String section,
            String grade,
            BigDecimal price
    ) {

    }
}
