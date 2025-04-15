package com.earlybird.ticket.concert.presentation.dto;

import com.earlybird.ticket.concert.application.dto.RegisterConcertCommand;
import com.earlybird.ticket.concert.application.dto.RegisterConcertCommand.ConcertSequenceCommand;
import com.earlybird.ticket.concert.application.dto.RegisterConcertCommand.SeatInstanceInfoCommand;
import com.earlybird.ticket.concert.domain.constant.ConcertStatus;
import com.earlybird.ticket.concert.domain.constant.Genre;
import com.earlybird.ticket.concert.domain.constant.Grade;
import com.earlybird.ticket.concert.domain.constant.Section;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

public record RegisterConcertRequest(

        String concertName,
        String entertainerName,
        LocalDateTime startDate,
        LocalDateTime endDate,
        String genre,
        Integer runningTime,
        Long sellerId,
        String priceInfo,
        List<SeatInstanceInfoRequest> seatInstanceInfoList,
        List<ConcertSequenceRequest> concertSequences
) {

    public RegisterConcertCommand toCommand() {
        return RegisterConcertCommand.builder()
                .concertName(concertName)
                .entertainerName(entertainerName)
                .startDate(startDate)
                .endDate(endDate)
                .genre(Genre.valueOf(genre.toUpperCase()))  // 문자열 → enum 변환
                .runningTime(runningTime)
                .sellerId(sellerId)
                .priceInfo(priceInfo)
                .seatInstanceInfoList(
                        seatInstanceInfoList.stream()
                                .map(seat -> SeatInstanceInfoCommand.builder()
                                        .section(seat.section())
                                        .grade(seat.grade())
                                        .price(seat.price())
                                        .build()
                                )
                                .toList()
                )
                .concertSequences(
                        concertSequences.stream()
                                .map(seq -> ConcertSequenceCommand.builder()
                                        .hallId(seq.hallId())
                                        .venueId(seq.venueId())
                                        .sequenceStartDate(seq.sequenceStartDate())
                                        .sequenceEndDate(seq.sequenceEndDate())
                                        .ticketSaleStartDate(seq.ticketSaleStartDate())
                                        .ticketSaleEndDate(seq.ticketSaleEndDate())
                                        .status(ConcertStatus.valueOf(seq.status().toUpperCase()))
                                        .build()
                                )
                                .toList()
                )
                .build();
    }

    public record SeatInstanceInfoRequest(
            Section section,
            Grade grade,
            Integer price
    ) {

    }

    public record ConcertSequenceRequest(
            UUID hallId,
            UUID venueId,
            LocalDateTime sequenceStartDate,
            LocalDateTime sequenceEndDate,
            LocalDateTime ticketSaleStartDate,
            LocalDateTime ticketSaleEndDate,
            String status
    ) {

    }
}
