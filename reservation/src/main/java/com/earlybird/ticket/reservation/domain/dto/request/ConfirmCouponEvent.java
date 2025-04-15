package com.earlybird.ticket.reservation.domain.dto.request;

import com.earlybird.ticket.common.entity.EventPayload;
import com.earlybird.ticket.common.entity.PassportDto;
import com.earlybird.ticket.common.entity.constant.Role;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ConfirmCouponEvent implements EventPayload {

    private UUID couponId;
    private PassportDto passportDto;

    @Builder
    public ConfirmCouponEvent(UUID couponId,
                              PassportDto passportDto) {
        this.couponId = couponId;
        this.passportDto = passportDto;
    }

    public static ConfirmCouponEvent createSeatPreemptPayload(UUID requestCouponId,
                                                              PassportDto passportDto) {
        return ConfirmCouponEvent.builder()
                                 .couponId(requestCouponId)
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