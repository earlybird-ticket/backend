package com.earlybird.ticket.concert.application.dto;

import com.earlybird.ticket.concert.domain.Concert;
import lombok.Builder;

@Builder
public record ProcessConcertDetailsQuery(

) {

    public static ProcessConcertDetailsQuery of(Concert concert) {
        return ProcessConcertDetailsQuery.builder().build();
    }
}
