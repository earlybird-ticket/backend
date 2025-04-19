package com.earlybird.ticket.payment.domain.entity.constant;

import java.util.Arrays;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum PaymentMethod {
    CARD(Method.CARD),
    SIMPLE_PAY(Method.SIMPLE_PAY),
    VIRTUAL_ACCOUNT(Method.VIRTUAL_ACCOUNT),
    ;

    private final String method;

    public static PaymentMethod from(String method) {
        return Arrays.stream(values())
            .filter(paymentMethod -> paymentMethod.getMethod().equals(method))
            .findFirst()
            .orElseThrow(() -> new IllegalArgumentException("Invalid payment method: " + method));
    }

    static class Method {

        public static final String CARD = "카드";
        public static final String SIMPLE_PAY = "간편결제";
        public static final String VIRTUAL_ACCOUNT = "가상계좌";
    }
}
