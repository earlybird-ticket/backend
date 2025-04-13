package com.earlybird.ticket.admin.application.event.dto;

import com.earlybird.ticket.admin.application.dto.DeleteVenueCommand;
import com.earlybird.ticket.common.entity.EventPayload;
import com.earlybird.ticket.common.entity.PassportDto;
import java.util.UUID;
import lombok.Builder;

@Builder
public record VenueDeletePayload(
        PassportDto passportDto,
        UUID venueId
) implements EventPayload {

    public static VenueDeletePayload toPayload(
            PassportDto passportDto, DeleteVenueCommand command) {
        return VenueDeletePayload.builder()
                .passportDto(passportDto)
                .venueId(command.venueId())
                .build();
    }
}
