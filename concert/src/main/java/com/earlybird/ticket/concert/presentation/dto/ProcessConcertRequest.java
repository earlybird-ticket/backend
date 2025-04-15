package com.earlybird.ticket.concert.presentation.dto;

import com.earlybird.ticket.concert.application.dto.ProcessConcertsCommand;

public record ProcessConcertRequest(

) {

    public ProcessConcertsCommand toCommand() {
        return ProcessConcertsCommand.builder()
                .build();
    }
}
