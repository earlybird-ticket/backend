package com.earlybird.ticket.admin.application.event.dto;

import com.earlybird.ticket.admin.application.dto.DeleteSeatInstanceCommand;
import com.earlybird.ticket.common.entity.EventPayload;
import com.earlybird.ticket.common.entity.PassportDto;
import java.util.UUID;
import lombok.Builder;

@Builder
public record SeatInstanceDeletePayload(
        PassportDto passportDto,
        UUID seatInstanceId
) implements EventPayload {

    public static SeatInstanceDeletePayload toPayload(
            PassportDto passportDto, DeleteSeatInstanceCommand command) {
        return SeatInstanceDeletePayload.builder()
                .passportDto(passportDto)
                .seatInstanceId(command.seatInstanceId())
                .build();
    }
}
