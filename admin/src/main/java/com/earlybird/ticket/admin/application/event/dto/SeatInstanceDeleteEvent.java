package com.earlybird.ticket.admin.application.event.dto;

import com.earlybird.ticket.admin.application.dto.DeleteSeatInstanceCommand;
import com.earlybird.ticket.common.entity.EventPayload;
import com.earlybird.ticket.common.entity.PassportDto;
import java.util.UUID;
import lombok.Builder;

@Builder
public record SeatInstanceDeleteEvent(
        PassportDto passportDto,
        UUID seatInstanceId
) implements EventPayload {

    public static SeatInstanceDeleteEvent toPayload(
            PassportDto passportDto, DeleteSeatInstanceCommand command) {
        return SeatInstanceDeleteEvent.builder()
                .passportDto(passportDto)
                .seatInstanceId(command.seatInstanceId())
                .build();
    }
}
