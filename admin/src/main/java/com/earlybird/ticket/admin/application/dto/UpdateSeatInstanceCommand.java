package com.earlybird.ticket.admin.application.dto;

import java.math.BigDecimal;
import java.util.UUID;
import lombok.Builder;

@Builder
public record UpdateSeatInstanceCommand(
        UUID seatInstanceId,
        UUID hallId,
        UUID concertId,
        UUID concertSequenceId,
        String grade,
        String status,
        BigDecimal price
) {

}
