package com.earlybird.ticket.concert.presentation.dto;

import com.earlybird.ticket.concert.application.dto.UpdateConcertSequenceCommand;
import com.earlybird.ticket.concert.domain.constant.ConcertStatus;
import java.time.LocalDateTime;
import java.util.UUID;

public record UpdateConcertSequenceRequest(
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

    public UpdateConcertSequenceCommand toCommand() {
        return UpdateConcertSequenceCommand.builder()
                .concertId(concertId)
                .concertSequenceId(concertSequenceId)
                .hallId(hallId)
                .venueId(venueId)
                .sequenceStartDate(sequenceStartDate)
                .sequenceEndDate(sequenceEndDate)
                .ticketSaleStartDate(ticketSaleStartDate)
                .ticketSaleEndDate(ticketSaleEndDate)
                .concertStatus(concertStatus)
                .build();
    }
}
