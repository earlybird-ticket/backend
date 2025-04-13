package com.earlybird.ticket.admin.application.event.dto;

import com.earlybird.ticket.admin.application.dto.RegisterVenueCommand;
import com.earlybird.ticket.common.entity.EventPayload;
import com.earlybird.ticket.common.entity.PassportDto;
import java.util.List;
import lombok.Builder;

@Builder
public record VenueCreatePayload(
        PassportDto passportDto,
        String venueName,
        String location,
        String area,
        int totalNumberOfSeat,
        List<HallCreatePayload> hallList
) implements EventPayload {

    public static VenueCreatePayload toPayload(PassportDto passport, RegisterVenueCommand command) {
        return VenueCreatePayload.builder()
                .passportDto(passport)
                .venueName(command.venueName())
                .location(command.location())
                .area(command.area())
                .totalNumberOfSeat(command.totalNumberOfSeat())
                .hallList(command.hallList().stream()
                                  .map((Hall) -> new HallCreatePayload(
                                          Hall.hallName(),
                                          Hall.hallFloor()
                                  )).toList())
                .build();
    }

    public record HallCreatePayload(
            String hallName,
            int hallFloor
    ) {

    }
}
