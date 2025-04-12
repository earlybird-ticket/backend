package com.earlybird.ticket.admin.presentation.dto;

import com.earlybird.ticket.admin.application.dto.RegisterSeatCommand;
import java.util.List;
import java.util.UUID;

public record RegisterSeatRequest(
        UUID hallId,
        UUID venueId,
        List<SeatInfo> seatList
) {

    public static List<RegisterSeatCommand.SeatInfo> toSeatInfo(List<SeatInfo> seatInfo) {
        return seatInfo.stream()
                .map(info -> RegisterSeatCommand.SeatInfo.builder()
                        .section(info.section())
                        .rowCnt(info.rowCnt())
                        .colCnt(info.colCnt())
                        .floor(info.floor())
                        .build()
                ).toList();
    }

    public RegisterSeatCommand toRegisterSeatCommand() {
        return RegisterSeatCommand.builder()
                .hallId(hallId)
                .venueId(venueId)
                .seatList(toSeatInfo(seatList))
                .build();
    }

    public record SeatInfo(
            String section,
            Integer rowCnt,
            Integer colCnt,
            Integer floor

    ) {

    }
}
