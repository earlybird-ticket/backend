package com.earlybird.ticket.reservation.application.dto.response;

import com.earlybird.ticket.common.entity.EventPayload;
import lombok.Builder;

import java.util.List;
import java.util.UUID;

@Builder
public record SeatPreemptFailEvent(List<UUID> seatInstanceIdList) implements EventPayload {
}