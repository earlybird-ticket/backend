package com.earlybird.ticket.concert.application.dto;

import com.earlybird.ticket.concert.domain.ConcertSequence;
import com.earlybird.ticket.concert.domain.constant.ConcertStatus;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import lombok.Builder;

@Builder
public record ProcessConcertDetailsQuery(
        List<ConcertSequenceResult> concertSequenceResults
) {

    public static ProcessConcertDetailsQuery of(List<ConcertSequence> concertSequences) {
        return ProcessConcertDetailsQuery.builder()
                .concertSequenceResults(
                        concertSequences.stream()
                                .map(concertSequence -> ConcertSequenceResult.builder()
                                        .concertSequenceId(concertSequence.getConcertSequenceId())
                                        .sequenceStartDate(concertSequence.getSequenceStartDate())
                                        .sequenceEndDate(concertSequence.getSequenceEndDate())
                                        .ticketSaleStartDate(
                                                concertSequence.getTicketSaleStartDate())
                                        .ticketSaleEndDate(concertSequence.getTicketSaleEndDate())
                                        .status(concertSequence.getConcertStatus())
                                        .build()
                                ).toList()
                )
                .build();
    }

    @Builder
    public record ConcertSequenceResult(
            UUID concertSequenceId,
            LocalDateTime sequenceStartDate,
            LocalDateTime sequenceEndDate,
            LocalDateTime ticketSaleStartDate,
            LocalDateTime ticketSaleEndDate,
            ConcertStatus status
    ) {

    }
}
