package com.earlybird.ticket.admin.presentation.dto;

import com.earlybird.ticket.admin.application.dto.UpdateVenueCommand;
import java.util.UUID;

public record UpdateVenueRequest(
        UUID venueId,
        String venueName,
        String location,
        String area,
        int totalNumberOfSeat
) {

    public UpdateVenueCommand toUpdateVenueCommand() {
        return UpdateVenueCommand.builder()
                .venueId(venueId)
                .venueName(venueName)
                .location(location)
                .area(area)
                .totalNumberOfSeat(totalNumberOfSeat)
                .build();
    }
}
