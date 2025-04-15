package com.earlybird.ticket.payment.application.service.exception;

import com.earlybird.ticket.common.entity.constant.Code;

public class PaymentTimeoutException extends AbstractPaymentException {

    public PaymentTimeoutException() {
        super("예매 시간 초과입니다. 다시 시도해주세요.", Code.TIMEOUT);
    }
}
