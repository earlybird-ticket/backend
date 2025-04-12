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
public class PreemptSeatPayload implements EventPayload {

    private List<UUID> seatInstanceList;
    private Passport passportDto;

    @Builder
    public PreemptSeatPayload(List<UUID> seatInstanceList,
                              Long userId,
                              Role role) {
        this.seatInstanceList = seatInstanceList;
        this.passportDto = new Passport(userId,
                                        role);
    }

    public static PreemptSeatPayload createSeatPreemptPayload(List<UUID> seatInstanceIds,
                                                              Long userId,
                                                              PassportDto passportDto) {
        return PreemptSeatPayload.builder()
                                 .seatInstanceList(seatInstanceIds)
                                 .userId(userId)
                                 .role(Role.from(passportDto.getUserRole()))
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