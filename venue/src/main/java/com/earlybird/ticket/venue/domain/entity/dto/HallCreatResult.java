package com.earlybird.ticket.venue.domain.entity.dto;

import lombok.Builder;

@Builder
public record HallCreatResult(
        String hallName,
        int hallFloor
) {
}
