package com.earlybird.ticket.admin.application.event.dto;

import com.earlybird.ticket.admin.application.dto.UpdateVenueCommand;
import com.earlybird.ticket.common.entity.EventPayload;
import com.earlybird.ticket.common.entity.PassportDto;
import java.util.UUID;
import lombok.Builder;

@Builder
public record VenueUpdatePayload(
        PassportDto passportDto,
        UUID venueId,
        String venueName,
        String location,
        String area,
        int totalNumberOfSeat
) implements EventPayload {

    public static VenueUpdatePayload toPayload(
            PassportDto passportDto, UpdateVenueCommand command) {
        return VenueUpdatePayload.builder()
                .passportDto(passportDto)
                .venueId(command.venueId())
                .venueName(command.venueName())
                .location(command.location())
                .area(command.area())
                .totalNumberOfSeat(command.totalNumberOfSeat())
                .build();
    }
}
