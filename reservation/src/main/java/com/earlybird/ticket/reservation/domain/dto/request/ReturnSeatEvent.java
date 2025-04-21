package com.earlybird.ticket.reservation.domain.dto.request;

import com.earlybird.ticket.common.entity.EventPayload;
import com.earlybird.ticket.common.entity.PassportDto;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.UUID;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ReturnSeatEvent implements EventPayload {

    private List<UUID> seatInstanceIdList;
    private PassportDto passportDto;

    @Builder
    public ReturnSeatEvent(List<UUID> seatInstanceIdList,
                           PassportDto passportDto) {
        this.seatInstanceIdList = seatInstanceIdList;
        this.passportDto = passportDto;
    }

    public static ReturnSeatEvent createSeatPreemptPayload(List<UUID> seatInstanceIds,
                                                           PassportDto passportDto) {
        return ReturnSeatEvent.builder()
                              .seatInstanceIdList(seatInstanceIds)
                              .passportDto(passportDto)
                              .build();
    }
}