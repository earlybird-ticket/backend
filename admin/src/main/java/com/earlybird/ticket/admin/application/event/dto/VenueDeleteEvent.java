package com.earlybird.ticket.admin.application.event.dto;

import com.earlybird.ticket.admin.application.dto.DeleteVenueCommand;
import com.earlybird.ticket.common.entity.EventPayload;
import com.earlybird.ticket.common.entity.PassportDto;
import java.util.UUID;
import lombok.Builder;

@Builder
public record VenueDeleteEvent(
        PassportDto passportDto,
        UUID venueId
) implements EventPayload {

    public static VenueDeleteEvent toPayload(
            PassportDto passportDto, DeleteVenueCommand command) {
        return VenueDeleteEvent.builder()
                .passportDto(passportDto)
                .venueId(command.venueId())
                .build();
    }
}
