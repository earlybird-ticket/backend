package com.earlybird.ticket.venue.application.event.dto.request;

import com.earlybird.ticket.common.entity.EventPayload;
import com.earlybird.ticket.common.entity.PassportDto;
import lombok.Builder;

import java.util.List;
import java.util.UUID;

@Builder
public record SeatReturnPayload(
        PassportDto passportDto,
        List<UUID> seatInstanceIdList,
        UUID reservationId,
        UUID concertSequenceId
) implements EventPayload {

    public static SeatReturnPayload toPayload(
            PassportDto passportDto,
            List<UUID> seatInstanceIdList,
            UUID reservationId,
            UUID concertSequenceId
    ) {
        return SeatReturnPayload.builder()
                .passportDto(passportDto)
                .seatInstanceIdList(seatInstanceIdList)
                .reservationId(reservationId)
                .concertSequenceId(concertSequenceId)
                .build();
    }
}
