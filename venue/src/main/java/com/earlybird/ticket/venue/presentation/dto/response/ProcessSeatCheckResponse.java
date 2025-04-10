package com.earlybird.ticket.venue.presentation.dto.response;

import com.earlybird.ticket.venue.application.dto.response.ProcessSeatCheckQuery;
import lombok.Builder;

import java.util.List;
import java.util.UUID;

@Builder
public record ProcessSeatCheckResponse(
        List<UUID> seatInstanceIdList,
        boolean status
        ) {

        public static ProcessSeatCheckResponse from(ProcessSeatCheckQuery processSeatCheckQuery) {
                return ProcessSeatCheckResponse.builder()
                        .seatInstanceIdList(processSeatCheckQuery.seatInstanceIdList())
                        .status(processSeatCheckQuery.status())
                        .build();
        }
}
