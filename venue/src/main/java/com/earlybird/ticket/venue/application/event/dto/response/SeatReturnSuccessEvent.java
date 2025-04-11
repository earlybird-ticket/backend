package com.earlybird.ticket.venue.application.event.dto.response;

import com.earlybird.ticket.common.entity.EventPayload;
import lombok.Builder;

import java.util.List;
import java.util.UUID;

@Builder
public record SeatReturnSuccessEvent(List<UUID> seatInstanceIdList) implements EventPayload {
}
