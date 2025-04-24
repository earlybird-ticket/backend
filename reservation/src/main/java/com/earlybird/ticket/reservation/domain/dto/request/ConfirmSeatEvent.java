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
public class ConfirmSeatEvent implements EventPayload {

    private List<UUID> seatInstanceIdList;
    private UUID concertSequenceId;
    private PassportDto passportDto;
    private UUID reservationId;

    @Builder
    public ConfirmSeatEvent(List<UUID> seatInstanceIdList,
                            UUID concertSequenceId,
                            PassportDto passportDto,
                            UUID reservationId) {
        this.seatInstanceIdList = seatInstanceIdList;
        this.concertSequenceId = concertSequenceId;
        this.passportDto = passportDto;
        this.reservationId = reservationId;
    }
}