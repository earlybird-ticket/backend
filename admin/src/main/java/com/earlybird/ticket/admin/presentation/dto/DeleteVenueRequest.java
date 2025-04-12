package com.earlybird.ticket.admin.presentation.dto;

import com.earlybird.ticket.admin.application.dto.DeleteVenueCommand;

public record DeleteVenueRequest() {

    public DeleteVenueCommand toDeleteVenueCommand() {
        return DeleteVenueCommand.builder().build();
    }
}
