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
    private PassportDto passport;

    @Builder
    public ConfirmCouponEvent(UUID couponId,
                              PassportDto passport) {
        this.couponId = couponId;
        this.passport = passport;
    }

    public static ConfirmCouponEvent createSeatPreemptPayload(UUID requestCouponId,
                                                              PassportDto passportDto) {
        return ConfirmCouponEvent.builder()
                                 .couponId(requestCouponId)
                                 .passport(passportDto)
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