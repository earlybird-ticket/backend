package com.earlybird.ticket.reservation.domain.entity.constant;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public enum PaymentMethod {
    CARD("CARD"),
    SIMPLE_PAY("SIMPLE"),
    VIRTUAL_PAY("VIRTURE"),
    POINT("POINT");

    private final String value;
}
