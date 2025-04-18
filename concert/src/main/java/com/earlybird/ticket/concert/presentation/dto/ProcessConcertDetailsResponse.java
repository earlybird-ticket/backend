package com.earlybird.ticket.concert.presentation.dto;

import com.earlybird.ticket.concert.application.dto.ProcessConcertDetailsQuery;
import com.earlybird.ticket.concert.domain.constant.ConcertStatus;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import lombok.Builder;

@Builder
public record ProcessConcertDetailsResponse(
        List<ConcertSequenceResult> concertSequenceResults
) {

    public static ProcessConcertDetailsResponse of(
            ProcessConcertDetailsQuery processConcertDetailsQuery
    ) {
        return ProcessConcertDetailsResponse.builder()
                .concertSequenceResults(processConcertDetailsQuery.concertSequenceResults()
                                                .stream()
                                                .map(concertSequenceResult -> ConcertSequenceResult.builder()
                                                        .concertSequenceId(
                                                                concertSequenceResult.concertSequenceId())
                                                        .sequenceStartDate(
                                                                concertSequenceResult.sequenceStartDate())
                                                        .sequenceEndDate(
                                                                concertSequenceResult.sequenceEndDate())
                                                        .ticketSaleStartDate(
                                                                concertSequenceResult.ticketSaleStartDate())
                                                        .ticketSaleEndDate(
                                                                concertSequenceResult.ticketSaleEndDate())
                                                        .status(concertSequenceResult.status())
                                                        .build()
                                                )
                                                .toList()
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
