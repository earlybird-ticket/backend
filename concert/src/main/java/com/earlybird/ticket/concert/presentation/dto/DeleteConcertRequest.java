package com.earlybird.ticket.concert.presentation.dto;

import com.earlybird.ticket.concert.application.dto.DeleteConcertCommand;
import java.util.UUID;

public record DeleteConcertRequest(
        UUID concertId

) {

    public DeleteConcertCommand toCommand() {
        return DeleteConcertCommand.builder()
                .concertId(concertId)
                .build();
    }
}
