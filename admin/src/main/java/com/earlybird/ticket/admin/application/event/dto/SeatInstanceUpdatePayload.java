package com.earlybird.ticket.admin.application.event.dto;

import com.earlybird.ticket.admin.application.dto.UpdateSeatInstanceCommand;
import com.earlybird.ticket.common.entity.EventPayload;
import com.earlybird.ticket.common.entity.PassportDto;
import java.math.BigDecimal;
import java.util.UUID;
import lombok.Builder;

@Builder
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

    public static SeatInstanceUpdatePayload toPayload(
            PassportDto passportDto, UpdateSeatInstanceCommand command) {
        return SeatInstanceUpdatePayload.builder()
                .passportDto(passportDto)
                .seatInstanceId(command.seatInstanceId())
                .hallId(command.hallId())
                .concertId(command.concertId())
                .concertSequenceId(command.concertSequenceId())
                .grade(command.grade())
                .status(command.status())
                .price(command.price())
                .build();
    }
}
