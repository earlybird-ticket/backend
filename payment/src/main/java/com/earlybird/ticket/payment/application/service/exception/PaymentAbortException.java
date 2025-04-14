package com.earlybird.ticket.payment.application.service.exception;

import com.earlybird.ticket.common.entity.constant.Code;

public class PaymentAbortException extends AbstractPaymentException {

    public PaymentAbortException() {
        super("결제 중 문제가 생겼습니다.", Code.BAD_REQUEST);
    }
}
