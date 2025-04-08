package com.earlybird.ticket.venue.application.dto.request;

import lombok.Builder;

import java.util.List;
import java.util.UUID;

public record ProcessSeatCheckCommand(
        List<UUID> seatIdList
) {
    @Builder
    public ProcessSeatCheckCommand(List<UUID> seatIdList) {
        this.seatIdList = seatIdList;
    }
}
