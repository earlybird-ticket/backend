package com.earlybird.ticket.admin.presentation.dto;

import com.earlybird.ticket.admin.application.dto.DeleteSeatInstanceCommand;

public record DeleteSeatInstanceRequest() {

    public DeleteSeatInstanceCommand toDeleteSeatCommand() {
        return DeleteSeatInstanceCommand.builder().build();
    }
}
