package com.earlybird.ticket.admin.application.dto;

import java.util.UUID;
import lombok.Builder;

@Builder
public record DeleteVenueCommand(
        UUID venueId
) {

}
