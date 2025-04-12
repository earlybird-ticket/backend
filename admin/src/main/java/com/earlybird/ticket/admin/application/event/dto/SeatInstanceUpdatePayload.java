package com.earlybird.ticket.admin.application.event.dto;

import com.earlybird.ticket.common.entity.EventPayload;
import com.earlybird.ticket.common.entity.PassportDto;
import java.math.BigDecimal;
import java.util.UUID;

public record SeatInstanceUpdatePayload(
        PassportDto passportDto,
        UUID seatInstanceId,
        UUID hallId,
        UUID concertId,
        UUID concertSequenceId,
        String grade,
        String status,
        BigDecimal price
) implements EventPayload {

}
