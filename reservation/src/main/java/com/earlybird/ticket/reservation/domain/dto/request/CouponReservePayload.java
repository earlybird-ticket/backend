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
public class CouponReservePayload implements EventPayload {

    private UUID couponId;
    private Passport passportDto;

    @Builder
    public CouponReservePayload(UUID couponId,
                                Long userId,
                                Role role) {
        this.couponId = couponId;
        this.passportDto = new Passport(userId,
                                        role);
    }

    public static CouponReservePayload createSeatPreemptPayload(UUID requestCouponId,
                                                                Long userId,
                                                                PassportDto passportDto) {
        return CouponReservePayload.builder()
                                   .couponId(requestCouponId)
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