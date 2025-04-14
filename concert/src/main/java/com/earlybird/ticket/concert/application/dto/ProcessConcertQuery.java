package com.earlybird.ticket.concert.application.dto;

import com.earlybird.ticket.concert.domain.Concert;
import lombok.Builder;

@Builder
public record ProcessConcertQuery(

) {

    public static ProcessConcertQuery of(Concert concert) {
        return ProcessConcertQuery.builder()
                .build();
    }
}
