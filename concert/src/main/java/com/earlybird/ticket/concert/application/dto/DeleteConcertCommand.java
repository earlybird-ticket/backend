package com.earlybird.ticket.concert.application.dto;

import java.util.UUID;
import lombok.Builder;

@Builder
public record DeleteConcertCommand(
        UUID concertId

) {

}
