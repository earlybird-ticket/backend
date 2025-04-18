package com.earlybird.ticket.concert.presentation.dto;

import com.earlybird.ticket.concert.application.dto.ProcessConcertQuery;
import com.earlybird.ticket.concert.domain.constant.Genre;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import lombok.Builder;

@Builder
public record ProcessConcertResponse(
        List<ConcertResponse> responses
) {


    public static ProcessConcertResponse of(ProcessConcertQuery processConcertQuery) {
        return ProcessConcertResponse.builder()
                .responses(List.of(ConcertResponse.of(processConcertQuery)))
                .build();
    }


    @Builder
    public record ConcertResponse(
            UUID concertId,
            String concertName,
            String entertainerName,
            LocalDateTime startDate,
            LocalDateTime endDate,
            Genre genre,
            Integer runningTime,
            String priceInfo
    ) {

        public static ConcertResponse of(ProcessConcertQuery processConcertQuery) {
            return ConcertResponse.builder()
                    .concertId(processConcertQuery.concertId())
                    .concertName(processConcertQuery.concertName())
                    .entertainerName(processConcertQuery.entertainerName())
                    .startDate(processConcertQuery.startDate())
                    .endDate(processConcertQuery.endDate())
                    .genre(processConcertQuery.genre())
                    .runningTime(processConcertQuery.runningTime())
                    .priceInfo(processConcertQuery.priceInfo())
                    .build();
        }
    }
}
