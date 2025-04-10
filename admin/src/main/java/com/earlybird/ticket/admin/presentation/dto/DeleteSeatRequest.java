package com.earlybird.ticket.admin.presentation.dto;

import com.earlybird.ticket.admin.application.dto.DeleteVenueCommand;

public record DeleteSeatRequest() {

    public DeleteVenueCommand toDeleteSeatCommand() {
        return new DeleteVenueCommand();
    }
}
