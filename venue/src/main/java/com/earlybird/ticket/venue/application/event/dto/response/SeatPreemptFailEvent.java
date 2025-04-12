package com.earlybird.ticket.venue.application.event.dto.response;

import com.earlybird.ticket.common.entity.EventPayload;
import com.earlybird.ticket.common.entity.PassportDto;
import lombok.Builder;

import java.util.List;
import java.util.UUID;

@Builder
public record SeatPreemptFailEvent(
        PassportDto passportDto,
        List<UUID> seatInstanceIdList,
        String code
) implements EventPayload {
}
