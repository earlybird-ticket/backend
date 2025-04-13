package com.earlybird.ticket.admin.presentation.dto;

import com.earlybird.ticket.admin.application.dto.DeleteVenueCommand;
import java.util.UUID;

public record DeleteVenueRequest(
        UUID venueId
) {

    public DeleteVenueCommand toDeleteVenueCommand() {
        return DeleteVenueCommand.builder().venueId(venueId).build();
    }
}
