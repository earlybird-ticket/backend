package com.earlybird.ticket.admin.application.dto;

import java.util.UUID;
import lombok.Builder;

@Builder
public record UpdateVenueCommand(
        UUID venueId,
        String venueName,
        String location,
        String area,
        int totalNumberOfSeat
) {

}
