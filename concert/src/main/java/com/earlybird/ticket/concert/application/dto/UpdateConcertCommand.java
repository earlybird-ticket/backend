package com.earlybird.ticket.concert.application.dto;

import com.earlybird.ticket.concert.domain.constant.Genre;
import java.time.LocalDateTime;
import java.util.UUID;
import lombok.Builder;

@Builder
public record UpdateConcertCommand(
        UUID concertId,
        String concertName,
        String entertainerName,
        LocalDateTime startDate,
        LocalDateTime endDate,
        Genre genre,
        Integer runningTime,
        String priceInfo
) {

}
