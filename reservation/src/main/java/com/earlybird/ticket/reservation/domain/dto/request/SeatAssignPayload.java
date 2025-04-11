package com.earlybird.ticket.reservation.domain.dto.request;

import com.earlybird.ticket.common.entity.EventPayload;
import com.earlybird.ticket.common.entity.constant.Role;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class SeatAssignPayload implements EventPayload {

    private UUID seatInstanceId;
    private Passport passportDto;

    @Builder
    public SeatAssignPayload(UUID seatInstanceId,
                             Long userId,
                             Role role) {
        this.seatInstanceId = seatInstanceId;
        this.passportDto = new Passport(userId,
                                        role);
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