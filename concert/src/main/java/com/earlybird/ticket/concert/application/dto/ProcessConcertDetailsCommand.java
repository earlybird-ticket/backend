package com.earlybird.ticket.concert.application.dto;

import java.util.UUID;
import lombok.Builder;

@Builder
public record ProcessConcertDetailsCommand(
        UUID concertId,
        UUID concertSequenceId
) {

}
