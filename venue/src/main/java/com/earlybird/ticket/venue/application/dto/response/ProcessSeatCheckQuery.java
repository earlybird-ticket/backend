package com.earlybird.ticket.venue.application.dto.response;

import lombok.Builder;

import java.util.List;
import java.util.UUID;

@Builder
public record ProcessSeatCheckQuery(
        List<UUID> seatInstanceIdList,
        boolean status
) {

    public static ProcessSeatCheckQuery from(
            List<UUID> seatInstanceIdList,
            boolean status
    ) {
        return ProcessSeatCheckQuery.builder()
                .seatInstanceIdList(seatInstanceIdList)
                .status(status)
                .build();
    }
}
