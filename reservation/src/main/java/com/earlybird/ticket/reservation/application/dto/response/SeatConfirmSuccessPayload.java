package com.earlybird.ticket.reservation.application.dto.response;

import com.earlybird.ticket.common.entity.EventPayload;
import com.earlybird.ticket.common.entity.PassportDto;
import lombok.Builder;

import java.util.List;
import java.util.UUID;

@Builder
public record SeatConfirmSuccessPayload(List<UUID> seatInstanceIdList,
                                        PassportDto passport,
                                        UUID reservationId,
                                        String code) implements EventPayload {
}