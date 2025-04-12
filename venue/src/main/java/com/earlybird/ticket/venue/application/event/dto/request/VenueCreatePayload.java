package com.earlybird.ticket.venue.application.event.dto.request;

import com.earlybird.ticket.common.entity.EventPayload;
import com.earlybird.ticket.common.entity.PassportDto;
import com.earlybird.ticket.venue.domain.entity.dto.HallCreatResult;

import java.util.ArrayList;
import java.util.List;

public record VenueCreatePayload(
        PassportDto passportDto,
        String venueName,
        String location,
        String area,
        int totalNumberOfSeat,
        List<HallCreatePayload> hallList
) implements EventPayload {

    public List<HallCreatResult> toHallCreateResult() {
        List<HallCreatResult> result = new ArrayList<>();

        this.hallList.stream()
                .forEach(payload -> result.add(
                        HallCreatResult.builder()
                                .hallName(payload.hallName())
                                .hallFloor(payload.hallFloor())
                                .build())
                );

        return result;
    }

    public static record HallCreatePayload(
            String hallName,
            int hallFloor
    ) {
    }
}
