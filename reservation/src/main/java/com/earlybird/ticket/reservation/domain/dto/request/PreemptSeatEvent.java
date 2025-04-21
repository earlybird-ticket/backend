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
public class PreemptSeatEvent implements EventPayload {

    private List<UUID> seatInstanceIdList;
    private PassportDto passportDto;
    private UUID reservationId;

    @Builder
    public PreemptSeatEvent(List<UUID> seatInstanceIdList,
                            PassportDto passportDto,
                            UUID reservationId) {
        this.seatInstanceIdList = seatInstanceIdList;
        this.passportDto = passportDto;
        this.reservationId = reservationId;
    }

    public static PreemptSeatEvent createSeatPreemptPayload(List<UUID> seatInstanceIds,
                                                            PassportDto passportDto,
                                                            UUID reservationId) {
        return PreemptSeatEvent.builder()
                               .seatInstanceIdList(seatInstanceIds)
                               .passportDto(passportDto)
                               .reservationId(reservationId)
                               .build();
    }
}