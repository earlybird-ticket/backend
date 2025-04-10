package com.earlybird.ticket.admin.presentation.dto;

import com.earlybird.ticket.admin.application.dto.UpdateVenueCommand;

public record UpdateSeatRequest() {

    public UpdateVenueCommand toUpdateSeatCommand() {
        return new UpdateVenueCommand();
    }
}
