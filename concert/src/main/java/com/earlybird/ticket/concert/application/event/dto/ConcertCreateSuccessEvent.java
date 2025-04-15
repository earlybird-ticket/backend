package com.earlybird.ticket.concert.application.event.dto;

import com.earlybird.ticket.common.entity.EventPayload;
import com.earlybird.ticket.common.entity.PassportDto;
import com.earlybird.ticket.concert.domain.Concert;
import com.earlybird.ticket.concert.domain.constant.ConcertStatus;
import com.earlybird.ticket.concert.domain.constant.Genre;
import com.earlybird.ticket.concert.domain.constant.Grade;
import com.earlybird.ticket.concert.domain.constant.Section;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import lombok.Builder;

@Builder
public record ConcertCreateSuccessEvent(
        PassportDto passportDto,
        String concertName,
        String entertainerName,
        LocalDateTime startDate,
        LocalDateTime endDate,
        Genre genre,
        Integer runningTime,
        Long sellerId,
        String priceInfo,
        List<SeatInstanceInfoEvent> seatInstanceInfoList,
        List<ConcertSequenceEvent> concertSequences
) implements EventPayload {

    public static ConcertCreateSuccessEvent toPayload(PassportDto passportDto, Concert concert) {
        return ConcertCreateSuccessEvent.builder()
                .passportDto(passportDto)
                .concertName(concert.getConcertName())
                .entertainerName(concert.getEntertainerName())
                .startDate(concert.getStartDate())
                .endDate(concert.getEndDate())
                .genre(concert.getGenre())
                .runningTime(concert.getRunningTime())
                .sellerId(concert.getSellerId())
                .priceInfo(concert.getPriceInfo())
                .seatInstanceInfoList(
                        concert.getSeatInstanceInfo()
                                .stream()
                                .map(info -> SeatInstanceInfoEvent.builder()
                                        .section(info.getSection())
                                        .grade(info.getGrade())
                                        .price(info.getPrice())
                                        .build()
                                )
                                .toList()
                )
                .concertSequences(
                        concert.getConcertSequences()
                                .stream()
                                .map(seq -> ConcertSequenceEvent.builder()
                                        .hallId(seq.getHallId())
                                        .venueId(seq.getVenueId())
                                        .sequenceStartDate(seq.getSequenceStartDate())
                                        .sequenceEndDate(seq.getSequenceEndDate())
                                        .ticketSaleStartDate(seq.getTicketSaleStartDate())
                                        .ticketSaleEndDate(seq.getTicketSaleEndDate())
                                        .status(seq.getConcertStatus())
                                        .build()
                                )
                                .toList()
                )
                .build();
    }

    @Builder
    public record SeatInstanceInfoEvent(
            Section section,
            Grade grade,
            Integer price
    ) {

    }

    @Builder
    public record ConcertSequenceEvent(
            UUID hallId,
            UUID venueId,
            LocalDateTime sequenceStartDate,
            LocalDateTime sequenceEndDate,
            LocalDateTime ticketSaleStartDate,
            LocalDateTime ticketSaleEndDate,
            ConcertStatus status
    ) {

    }
}
