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
public class ConfirmCouponPayload implements EventPayload {

    private UUID couponId;
    private PassportDto passport;

    @Builder
    public ConfirmCouponPayload(UUID couponId,
                                Long userId,
                                String userRole) {
        this.couponId = couponId;
        this.passport = new PassportDto(userId,
                                        userRole);
    }

    public static ConfirmCouponPayload createSeatPreemptPayload(UUID requestCouponId,
                                                                Long userId,
                                                                PassportDto passportDto) {
        return ConfirmCouponPayload.builder()
                                   .couponId(requestCouponId)
                                   .userId(userId)
                                   .userRole(passportDto.getUserRole())
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