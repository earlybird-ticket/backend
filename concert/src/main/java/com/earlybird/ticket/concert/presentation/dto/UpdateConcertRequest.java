package com.earlybird.ticket.concert.presentation.dto;

import com.earlybird.ticket.concert.application.dto.UpdateConcertCommand;
import com.earlybird.ticket.concert.domain.constant.Genre;
import java.time.LocalDateTime;
import java.util.UUID;

public record UpdateConcertRequest(
        UUID concertId,
        String concertName,
        String entertainerName,
        LocalDateTime startDate,
        LocalDateTime endDate,
        Genre genre,
        Integer runningTime,
        String priceInfo

) {

    public UpdateConcertCommand toCommand() {
        return UpdateConcertCommand.builder()
                .concertId(concertId)
                .concertName(concertName)
                .entertainerName(entertainerName)
                .startDate(startDate)
                .endDate(endDate)
                .genre(genre)
                .runningTime(runningTime)
                .priceInfo(priceInfo)
                .build();
    }
}
