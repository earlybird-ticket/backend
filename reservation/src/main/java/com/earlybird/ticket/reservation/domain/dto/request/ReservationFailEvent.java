package com.earlybird.ticket.reservation.domain.dto.request;

import com.earlybird.ticket.common.entity.EventPayload;
import com.earlybird.ticket.common.entity.PassportDto;
import lombok.Builder;

import java.util.List;
import java.util.UUID;

@Builder
public record ReservationFailEvent(List<UUID> seatInstanceIdList,
                                   PassportDto passportDto,
                                   UUID reservationId,
                                   String code) implements EventPayload {
}