package com.earlybird.ticket.venue.application.dto.request;

import com.earlybird.ticket.venue.domain.entity.SeatInstance;
import com.earlybird.ticket.venue.domain.entity.constant.Status;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;

import java.math.BigDecimal;
import java.util.UUID;


@Builder
public record PrepareSeatIndexCommand(
        @JsonProperty(value = "seat_instance_id")
        UUID seatInstanceId,
        int row,
        int col,
        Status status,
        BigDecimal price
        ) {
    public static PrepareSeatIndexCommand fromSeatInstance(SeatInstance seatInstance) {
        return PrepareSeatIndexCommand.builder()
                .seatInstanceId(seatInstance.getId())
                .row(seatInstance.getSeat().getRow())
                .col(seatInstance.getSeat().getCol())
                .status(seatInstance.getStatus())
                .price(seatInstance.getPrice())
                .build();
    }
}
