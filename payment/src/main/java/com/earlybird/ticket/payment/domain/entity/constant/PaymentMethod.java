package com.earlybird.ticket.payment.domain.entity.constant;

import java.util.Arrays;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum PaymentMethod {
    CREDIT_CARD(Method.CREDIT_CARD),
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

        public static final String CREDIT_CARD = "카드";
        public static final String VIRTUAL_ACCOUNT = "가상계좌";
    }
}
