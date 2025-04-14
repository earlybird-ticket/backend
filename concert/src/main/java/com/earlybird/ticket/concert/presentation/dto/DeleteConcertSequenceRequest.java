package com.earlybird.ticket.concert.presentation.dto;

import com.earlybird.ticket.concert.application.dto.DeleteConcertSequenceCommand;
import java.util.UUID;

public record DeleteConcertSequenceRequest(
        UUID concertId,
        UUID concertSequenceId
) {

    public DeleteConcertSequenceCommand toCommand() {
        return DeleteConcertSequenceCommand.builder()
                .concertId(concertId)
                .concertSequenceId(concertSequenceId)
                .build();
    }
}
