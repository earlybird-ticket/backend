package com.earlybird.ticket.admin.presentation.dto;

import com.earlybird.ticket.admin.application.dto.UpdateSeatInstanceCommand;
import java.math.BigDecimal;
import java.util.UUID;

public record UpdateSeatInstanceRequest(
        UUID seatInstanceId,
        UUID hallId,
        UUID concertId,
        UUID concertSequenceId,
        String grade,
        String status,
        BigDecimal price
) {

    public UpdateSeatInstanceCommand toUpdateSeatCommand() {
        return UpdateSeatInstanceCommand.builder()
                .seatInstanceId(seatInstanceId)
                .hallId(hallId)
                .concertId(concertId)
                .concertSequenceId(concertSequenceId)
                .grade(grade)
                .status(status)
                .price(price)
                .build();
    }
}
