package com.earlybird.ticket.admin.application.event.dto;

import com.earlybird.ticket.admin.application.dto.RegisterSeatCommand;
import com.earlybird.ticket.common.entity.EventPayload;
import com.earlybird.ticket.common.entity.PassportDto;
import java.util.List;
import java.util.UUID;
import lombok.Builder;

@Builder
public record SeatCreateEvent(
        PassportDto passportDto,
        UUID hallId,
        UUID venueId,
        List<SeatInfo> seatList
) implements EventPayload {

    public static EventPayload toPayload(PassportDto passport, RegisterSeatCommand command) {
        return SeatCreateEvent.builder()
                .passportDto(passport)
                .hallId(command.hallId())
                .venueId(command.venueId())
                .seatList(command.seatList().stream()
                                  .map(seatInfo -> new SeatCreateEvent.SeatInfo(
                                          seatInfo.section(), seatInfo.rowCnt(), seatInfo.colCnt(),
                                          seatInfo.floor()
                                  ))
                                  .toList())
                .build();
    }

    public record SeatInfo(
            String section,
            Integer rowCnt,
            Integer colCnt,
            Integer floor

    ) {

    }
}
