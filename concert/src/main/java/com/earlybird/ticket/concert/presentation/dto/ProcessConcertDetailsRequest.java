package com.earlybird.ticket.concert.presentation.dto;

import com.earlybird.ticket.concert.application.dto.ProcessConcertDetailsCommand;

public record ProcessConcertDetailsRequest(

) {

    public ProcessConcertDetailsCommand toCommand() {
        return ProcessConcertDetailsCommand.builder()
                .build();
    }
}
