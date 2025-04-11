package com.earlybird.ticket.venue.application.event.dto.request;

import com.earlybird.ticket.common.entity.EventPayload;
import com.earlybird.ticket.common.entity.PassportDto;

import java.util.UUID;

public record SeatInstanceDeletePayload(
        PassportDto passportDto,
        UUID seatInstanceId
) implements EventPayload {
}
