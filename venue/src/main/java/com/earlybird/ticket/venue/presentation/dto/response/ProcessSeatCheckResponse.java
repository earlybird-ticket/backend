package com.earlybird.ticket.venue.presentation.dto.response;

import com.earlybird.ticket.venue.application.dto.response.ProcessSeatCheckQuery;
import lombok.Builder;

import java.util.List;
import java.util.UUID;

@Builder
public record ProcessSeatCheckResponse(
        List<UUID> seatIdList,
        boolean status
        ) {

        public static ProcessSeatCheckResponse from(ProcessSeatCheckQuery processSeatCheckQuery) {
                return ProcessSeatCheckResponse.builder()
                        .seatIdList(processSeatCheckQuery.seatIdList())
                        .status(processSeatCheckQuery.status())
                        .build();
        }
}
