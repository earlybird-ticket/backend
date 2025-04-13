package com.earlybird.ticket.admin.presentation.dto;

import com.earlybird.ticket.admin.application.dto.DeleteSeatInstanceCommand;
import java.util.UUID;

public record DeleteSeatInstanceRequest(
        UUID seatInstanceId
) {

    public DeleteSeatInstanceCommand toDeleteSeatCommand() {
        return DeleteSeatInstanceCommand.builder().seatInstanceId(seatInstanceId).build();
    }
}
