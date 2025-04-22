package com.earlybird.ticket.reservation.domain.dto.request;

import com.earlybird.ticket.common.entity.EventPayload;
import com.earlybird.ticket.common.entity.PassportDto;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ReturnCouponEvent implements EventPayload {

    private UUID couponId;
    private PassportDto passportDto;
    private String code;

    @Builder
    public ReturnCouponEvent(UUID couponId,
                             PassportDto passportDto,
                             String code) {
        this.couponId = couponId;
        this.passportDto = passportDto;
        this.code = code;
    }
}