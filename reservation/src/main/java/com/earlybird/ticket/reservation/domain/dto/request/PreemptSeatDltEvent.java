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
public class PreemptSeatDltEvent implements EventPayload {

    private List<UUID> seatInstanceIdList;
    private PassportDto passportDto;

    @Builder
    public PreemptSeatDltEvent(List<UUID> seatInstanceIdList,
                               PassportDto passportDto) {
        this.seatInstanceIdList = seatInstanceIdList;
        this.passportDto = passportDto;
    }

    public static PreemptSeatDltEvent createSeatPreemptPayload(List<UUID> seatInstanceIds,
                                                               PassportDto passportDto) {
        return PreemptSeatDltEvent.builder()
                                  .seatInstanceIdList(seatInstanceIds)
                                  .passportDto(passportDto)
                                  .build();
    }


}