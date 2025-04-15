package com.earlybird.ticket.reservation.domain.dto.request;

import com.earlybird.ticket.common.entity.EventPayload;
import com.earlybird.ticket.common.entity.PassportDto;
import com.earlybird.ticket.common.entity.constant.Role;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.UUID;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class PreemptSeatDltEvent implements EventPayload {

    private List<UUID> seatInstanceList;
    private PassportDto passportDto;

    @Builder
    public PreemptSeatDltEvent(List<UUID> seatInstanceList,
                               PassportDto passportDto) {
        this.seatInstanceList = seatInstanceList;
        this.passportDto = passportDto;
    }

    public static PreemptSeatDltEvent createSeatPreemptPayload(List<UUID> seatInstanceIds,
                                                               PassportDto passportDto) {
        return PreemptSeatDltEvent.builder()
                                  .seatInstanceList(seatInstanceIds)
                                  .passportDto(passportDto)
                                  .build();
    }

    @Getter
    @NoArgsConstructor(access = AccessLevel.PROTECTED)
    public static class Passport {
        private Long userId;
        private Role role;

        public Passport(Long userId,
                        Role role) {
            this.userId = userId;
            this.role = role;
        }
    }
}