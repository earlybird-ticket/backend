package com.earlybird.ticket.admin.presentation.dto;

import com.earlybird.ticket.admin.application.dto.UpdateVenueCommand;

public record UpdateVenueRequest(

) {

    public UpdateVenueCommand toUpdateVenueCommand() {
        return UpdateVenueCommand.builder()
                .build();
    }
}
