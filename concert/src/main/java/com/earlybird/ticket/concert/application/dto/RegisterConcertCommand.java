package com.earlybird.ticket.concert.application.dto;

import com.earlybird.ticket.concert.domain.Concert;
import com.earlybird.ticket.concert.domain.ConcertSequence;
import com.earlybird.ticket.concert.domain.SeatInstanceInfo;
import com.earlybird.ticket.concert.domain.constant.ConcertStatus;
import com.earlybird.ticket.concert.domain.constant.Genre;
import com.earlybird.ticket.concert.domain.constant.Grade;
import com.earlybird.ticket.concert.domain.constant.Section;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import lombok.Builder;

@Builder
public record RegisterConcertCommand(
        String concertName,
        String entertainerName,
        LocalDateTime startDate,
        LocalDateTime endDate,
        UUID hallId,
        UUID venueId,
        Genre genre,
        Integer runningTime,
        Long sellerId,
        String priceInfo,
        List<SeatInstanceInfoCommand> seatInstanceInfoList,
        List<ConcertSequenceCommand> concertSequences
) {

    public Concert toEntity() {

        return Concert.builder()
                .hallId(hallId)
                .venueId(venueId)
                .concertName(concertName())
                .entertainerName(entertainerName())
                .startDate(startDate())
                .endDate(endDate())
                .genre(genre())
                .runningTime(runningTime())
                .sellerId(sellerId())
                .priceInfo(priceInfo())
                .concertSequences(concertSequences()
                                          .stream()
                                          .map(seq -> ConcertSequence.builder()
                                                  .sequenceStartDate(seq.sequenceStartDate())
                                                  .sequenceEndDate(seq.sequenceEndDate())
                                                  .ticketSaleStartDate(seq.ticketSaleStartDate())
                                                  .ticketSaleEndDate(seq.ticketSaleEndDate())
                                                  .concertStatus(seq.status())
                                                  .build()
                                          )
                                          .toList()
                )
                .seatInstanceInfo(seatInstanceInfoList()
                                          .stream()
                                          .map(info -> SeatInstanceInfo.builder()
                                                  .section(info.section())
                                                  .grade(info.grade())
                                                  .price(info.price())
                                                  .build()
                                          )
                                          .toList()
                )
                .build();
    }

    @Builder
    public record SeatInstanceInfoCommand(
            Section section,
            Grade grade,
            Integer price
    ) {

    }

    @Builder
    public record ConcertSequenceCommand(
            LocalDateTime sequenceStartDate,
            LocalDateTime sequenceEndDate,
            LocalDateTime ticketSaleStartDate,
            LocalDateTime ticketSaleEndDate,
            ConcertStatus status
    ) {

    }
}
