package com.earlybird.ticket.concert.application.dto;

import com.earlybird.ticket.concert.domain.Concert;
import com.earlybird.ticket.concert.domain.constant.Genre;
import java.time.LocalDateTime;
import java.util.UUID;
import lombok.Builder;

@Builder
public record ProcessConcertQuery(
        UUID concertId,
        String concertName,
        String entertainerName,
        LocalDateTime startDate,
        LocalDateTime endDate,
        Genre genre,
        Integer runningTime,
        String priceInfo
) {

    public static ProcessConcertQuery of(Concert concert) {
        return ProcessConcertQuery.builder()
                .concertId(concert.getConcertId())
                .concertName(concert.getConcertName())
                .entertainerName(concert.getEntertainerName())
                .startDate(concert.getStartDate())
                .endDate(concert.getEndDate())
                .genre(concert.getGenre())
                .runningTime(concert.getRunningTime())
                .priceInfo(concert.getPriceInfo())
                .build();
    }

}
