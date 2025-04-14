package com.earlybird.ticket.concert.application.dto;

import com.earlybird.ticket.concert.domain.constant.ConcertStatus;
import java.time.LocalDateTime;
import java.util.UUID;
import lombok.Builder;

@Builder
public record UpdateConcertSequenceCommand(

        UUID concertId,
        UUID concertSequenceId,
        UUID hallId,
        UUID venueId,
        LocalDateTime sequenceStartDate,
        LocalDateTime sequenceEndDate,
        LocalDateTime ticketSaleStartDate,
        LocalDateTime ticketSaleEndDate,
        ConcertStatus concertStatus
) {

}
