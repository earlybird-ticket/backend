package com.earlybird.ticket.reservation.application.dto.response;

import com.earlybird.ticket.common.entity.EventPayload;
import com.earlybird.ticket.common.entity.PassportDto;
import lombok.Builder;

import java.util.List;
import java.util.UUID;

@Builder
public record SeatReturnFailPayload(List<UUID> seatInstanceIdList,
                                    PassportDto passportDto,
                                    String code) implements EventPayload {
}