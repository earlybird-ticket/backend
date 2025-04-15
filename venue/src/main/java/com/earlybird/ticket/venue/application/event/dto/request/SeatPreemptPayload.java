package com.earlybird.ticket.venue.application.event.dto.request;

import com.earlybird.ticket.common.entity.EventPayload;
import com.earlybird.ticket.common.entity.PassportDto;

import java.util.List;
import java.util.UUID;

public record SeatPreemptPayload(
        PassportDto passportDto,
        List<UUID> seatInstanceIdList,
        UUID reservationId
) implements EventPayload {
}
